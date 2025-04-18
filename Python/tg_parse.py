from telethon.sync import TelegramClient
from telethon.tl.functions.messages import GetDialogsRequest, GetHistoryRequest
from telethon.tl.types import InputPeerEmpty
from kafka import KafkaProducer
import redis
import time
import json
import base64
import io

# Укажите свои данные API
api_id = ------------
api_hash = "----------------"
phone = "-----------------"

# Создаем клиент Telegram
client = TelegramClient(phone, api_id, api_hash)
client.start()

# Создаем продюсера Kafka
producer = KafkaProducer(bootstrap_servers='localhost:9092',
                         value_serializer=lambda v: json.dumps(v).encode('utf-8'))

# Подключаемся к Redis
redis_client = redis.Redis(host='localhost', port=6379, db=0)

# Получаем список чатов
result = client(GetDialogsRequest(
    offset_date=None,
    offset_id=0,
    offset_peer=InputPeerEmpty(),
    limit=1200,
    hash=0
))

# Фильтруем чаты на группы
groups = [chat for chat in result.chats if hasattr(chat, 'megagroup') and chat.megagroup]

# Выводим список групп
print("Выберите группу для отслеживания сообщений:")
for i, group in enumerate(groups):
    print(f"{i} - {group.title}")

# Пользователь выбирает группу
g_index = int(input("Введите нужную цифру: "))
target_group = groups[g_index]

# Получаем последние 400 сообщений из группы
print(f"Получаем последние 400 сообщений из группы {target_group.title}...")
history = client(GetHistoryRequest(
    peer=target_group,
    offset_id=0,
    offset_date=None,
    add_offset=0,
    limit=400,  # Получаем последние 400 сообщений
    max_id=0,
    min_id=0,
    hash=0
))

# Обрабатываем и выводим последние 400 сообщений
if history.messages:
    for message in reversed(history.messages):
        message_data = {
            'text': message.message if hasattr(message, 'message') else "Новое сообщение (тип не поддерживается)",
            'sender': message.from_id.user_id if hasattr(message, 'from_id') else "Неизвестный отправитель",
            'time': message.date.strftime('%Y-%m-%d %H:%M:%S'),
            'image': None  # Изначально устанавливаем image в None
        }
        # Проверяем наличие вложений (например, изображения)
        if message.media and hasattr(message.media, 'photo'):
            file = client.download_media(message.media)
            with open(file, "rb") as img_file:
                img_byte_array = io.BytesIO(img_file.read())
                img_base64 = base64.b64encode(img_byte_array.getvalue()).decode('utf-8')
                message_data['image'] = img_base64  # Добавляем изображение в формате base64
        print("Сообщение с медиа получено" if message_data['image'] else "Сообщение без медиа получено")

        # Отправляем сообщение в Kafka
        producer.send('messages', message_data)

        # Сохраняем сообщение в Redis с TTL 24 часа (86400 секунд)
        redis_key = f"message:{message.id}"
        redis_client.set(redis_key, json.dumps(message_data), ex=86400)

    # Устанавливаем last_message_id на ID последнего сообщения
    last_message_id = history.messages[0].id
else:
    print("В группе нет сообщений.")
    last_message_id = 0

# Начинаем отслеживание новых сообщений
print(f"Начинаем отслеживание новых сообщений в группе {target_group.title}...")

while True:
    time.sleep(5)  # Добавляем задержку, чтобы избежать перегрузки сервера
    history = client(GetHistoryRequest(
        peer=target_group,
        offset_id=0,
        offset_date=None,
        add_offset=0,
        limit=100,  # Получаем новые сообщения
        max_id=0,
        min_id=last_message_id,
        hash=0
    ))
    if history.messages:  # Проверяем наличие новых сообщений
        for message in reversed(history.messages):
            if message.id > last_message_id:
                message_data = {
                    'text': message.message if hasattr(message, 'message') else "Новое сообщение (тип не поддерживается)",
                    'sender': message.from_id.user_id if hasattr(message, 'from_id') else "Неизвестный отправитель",
                    'time': message.date.strftime('%Y-%m-%d %H:%M:%S'),
                    'image': None  # Изначально устанавливаем image в None
                }
                # Проверяем наличие вложений (например, изображения)
                if message.media and hasattr(message.media, 'photo'):
                    file = client.download_media(message.media)
                    with open(file, "rb") as img_file:
                        img_byte_array = io.BytesIO(img_file.read())
                        img_base64 = base64.b64encode(img_byte_array.getvalue()).decode('utf-8')
                        message_data['image'] = img_base64  # Добавляем изображение в формате base64
                print("Сообщение с медиа получено" if message_data['image'] else "Сообщение без медиа получено")

                # Отправляем сообщение в Kafka
                producer.send('messages', message_data)

                # Сохраняем сообщение в Redis с TTL 24 часа (86400 секунд)
                redis_key = f"message:{message.id}"
                redis_client.set(redis_key, json.dumps(message_data), ex=86400)

                last_message_id = message.id  # Обновляем last_message_id
    else:
        print("Нет новых сообщений.")
