package com.example.LLMCodingChallenge2025.Controller.REST;

import com.example.LLMCodingChallenge2025.Model.Info.Info;
import com.example.LLMCodingChallenge2025.Service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/info")
public class InfoRESTController {

    @Autowired
    private InfoService infoService;

    // Получить все данные
    @GetMapping
    public List<Info> getAllInfo() {
        return infoService.getAllInfo();
    }

    // Получить конкретную запись по ID
    @GetMapping("/{id}")
    public Info getConcreteInfo(@PathVariable int id) {
        return infoService.getConcreteInfo(id);
    }

    // Сохранить запись
    @PostMapping
    public Info saveInfo(@RequestBody Info info) {
        return infoService.saveInfo(info);
    }

    // Удалить запись по ID
    @DeleteMapping("/{id}")
    public void deleteInfo(@PathVariable int id) {
        infoService.deleteInfo(id);
    }

    // Получить данные для линейной диаграммы
    @GetMapping("/linear-chart")
    public List<Object[]> getDataForLinearChart() {
        return infoService.getDataForLinearChart();
    }

    // Получить данные для второй линейной диаграммы
    @GetMapping("/second-linear-chart")
    public List<Object[]> getDataForSecondLinearChart() {
        return infoService.getDataForSecondLinearChart();
    }

    // Получить данные для диаграммы Ганта
    @GetMapping("/gantt-chart")
    public List<Object[]> getDataForGanttChart() {
        return infoService.getDataForGanttChart();
    }

    // Получить данные для графа
    @GetMapping("/graph")
    public List<Object[]> getDataForGraph() {
        return infoService.getDataForGraph();
    }
}
