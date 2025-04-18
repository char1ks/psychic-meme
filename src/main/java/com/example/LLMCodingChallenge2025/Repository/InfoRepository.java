package com.example.LLMCodingChallenge2025.Repository;

import com.example.LLMCodingChallenge2025.Model.Info.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.LLMCodingChallenge2025.Model.Info.Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfoRepository extends JpaRepository<Info, Long> {

    // Получить данные для линейной диаграммы (динамика выполнения операций по дням)
    @Query("SELECT i.date, i.per_day_ga, i.from_start_ga FROM Info i ORDER BY i.date")
    List<Object[]> getDataForLinearChart();

    // Получить данные для второй линейной диаграммы (динамика валового сбора по дням)
    @Query("SELECT i.date, i.val_day_ga, i.val_start_ga FROM Info i ORDER BY i.date")
    List<Object[]> getDataForSecondLinearChart();

    // Получить данные для диаграммы Ганта (график выполнения операций)
    @Query("SELECT i.subdivision, i.operation, i.date FROM Info i ORDER BY i.date")
    List<Object[]> getDataForGanttChart();

    // Получить данные для графа (связь подразделений и операций)
    @Query("SELECT DISTINCT i.subdivision, i.operation FROM Info i")
    List<Object[]> getDataForGraph();

}
