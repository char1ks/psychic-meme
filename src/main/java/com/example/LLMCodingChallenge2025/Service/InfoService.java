package com.example.LLMCodingChallenge2025.Service;

import com.example.LLMCodingChallenge2025.Model.Info.Info;
import com.example.LLMCodingChallenge2025.Repository.InfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    // Получить все данные
    public List<Info> getAllInfo() {
        return infoRepository.findAll();
    }

    // Получить конкретную запись по ID
    public Info getConcreteInfo(int id) {
        return infoRepository.findById((long) id).orElse(null);
    }

    // Сохранить запись
    public Info saveInfo(Info info) {
        return infoRepository.save(info);
    }

    // Удалить запись по ID
    public void deleteInfo(int id) {
        infoRepository.deleteById((long) id);
    }

    // Получить данные для линейной диаграммы (динамика выполнения операций по дням)
    public List<Object[]> getDataForLinearChart() {
        return infoRepository.getDataForLinearChart();
    }

    // Получить данные для второй линейной диаграммы (динамика валового сбора по дням)
    public List<Object[]> getDataForSecondLinearChart() {
        return infoRepository.getDataForSecondLinearChart();
    }

    // Получить данные для диаграммы Ганта (график выполнения операций)
    public List<Object[]> getDataForGanttChart() {
        return infoRepository.getDataForGanttChart();
    }

    // Получить данные для графа (связь подразделений и операций)
    public List<Object[]> getDataForGraph() {
        return infoRepository.getDataForGraph();
    }
}
