package cm.bogne_stanley.money_flow.domain.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import cm.bogne_stanley.money_flow.common.exception.CustomValidationException;
import cm.bogne_stanley.money_flow.data.entity.Category;
import cm.bogne_stanley.money_flow.data.entity.Expense;
import cm.bogne_stanley.money_flow.data.entity.ExpenseType;
import cm.bogne_stanley.money_flow.data.entity.User;
import cm.bogne_stanley.money_flow.data.repository.ExpenseRepository;
import cm.bogne_stanley.money_flow.presentation.dto.request.stats.CompareStatsRequest;
import cm.bogne_stanley.money_flow.presentation.dto.request.stats.StatsFilter;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.CategoryStats;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.CompareStatsResponse;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.EvolutionStats;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.PeriodInfo;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.StatsResponse;
import cm.bogne_stanley.money_flow.presentation.dto.response.stats.TypeStats;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final ExpenseRepository expenseRepository;

    public StatsResponse getStats(StatsFilter filter) {
        User currentUser = getCurrentUser();
        
        // Par défaut, utiliser le mois en cours
        LocalDate now = LocalDate.now();
        LocalDate defaultStartDate = now.withDayOfMonth(1);
        LocalDate defaultEndDate = now.withDayOfMonth(now.lengthOfMonth());
        
        // Déterminer la période
        LocalDate startDate = defaultStartDate;
        LocalDate endDate = defaultEndDate;
        
        if (filter != null) {
            LocalDate filterStartDate = filter.startDate();
            LocalDate filterEndDate = filter.endDate();
            
            // Si des dates sont fournies, les utiliser
            if (filterStartDate != null || filterEndDate != null) {
                // Validation : si une date est fournie, l'autre doit l'être aussi
                if ((filterStartDate == null && filterEndDate != null) || (filterStartDate != null && filterEndDate == null)) {
                    throw new CustomValidationException(List.of(Map.of("invalid_date", "Both start date and end date must be provided")));
                }
                
                // Les deux dates sont définies
                if (filterStartDate != null && filterEndDate != null) {
                    startDate = filterStartDate;
                    endDate = filterEndDate;
                    
                    if (startDate.isAfter(endDate)) {
                        throw new CustomValidationException(List.of(Map.of("invalid_date", "End date must be before the start date")));
                    }
                }
            }
        }
        
        // Récupérer les dépenses de la période
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC);
        
        List<Expense> expenses = expenseRepository.findAllByUserAndCreatedAtBetween(
            currentUser, startInstant, endInstant
        );
        
        // Appliquer les filtres optionnels
        if (filter != null) {
            if (filter.type() != null && !filter.type().isBlank()) {
                ExpenseType expenseType = ExpenseType.valueOf(filter.type());
                expenses = expenses.stream()
                    .filter(e -> e.getType() == expenseType)
                    .collect(Collectors.toList());
            }
            
            if (filter.category_id() != null) {
                expenses = expenses.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().getId().equals(filter.category_id()))
                    .collect(Collectors.toList());
            }
        }
        
        // Calculer les statistiques
        return calculateStats(expenses, startDate, endDate, filter);
    }
    
    public CompareStatsResponse compareStats(CompareStatsRequest request) {
        List<CompareStatsResponse.PeriodStats> periodStatsList = new ArrayList<>();
        
        for (CompareStatsRequest.Period period : request.periods()) {
            // Créer un StatsFilter à partir de la période
            StatsFilter filter = new StatsFilter(
                period.start_date(),
                period.end_date(),
                period.type(),
                period.category_id()
            );
            
            StatsResponse stats = getStats(filter);
            PeriodInfo periodInfo = new PeriodInfo(
                period.start_date(),
                period.end_date()
            );
            
            periodStatsList.add(new CompareStatsResponse.PeriodStats(periodInfo, stats));
        }
        
        return new CompareStatsResponse(periodStatsList);
    }
    
    private StatsResponse calculateStats(List<Expense> expenses, LocalDate startDate, LocalDate endDate, StatsFilter filter) {
        // Métriques de base
        Double totalAmount = expenses.stream()
            .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
            .sum();
        Long totalCount = (long) expenses.size();
        Double averageAmount = totalCount > 0 ? totalAmount / totalCount : 0.0;
        
        // Statistiques par catégorie
        List<CategoryStats> byCategory = calculateCategoryStats(expenses, totalAmount);
        
        // Statistiques par type
        List<TypeStats> byType = calculateTypeStats(expenses, totalAmount);
        
        // Informations sur la période
        PeriodInfo periodInfo = new PeriodInfo(
            startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        
        // Évolution (comparaison avec la période précédente)
        EvolutionStats evolution = calculateEvolution(startDate, endDate, totalAmount, filter);
        
        return new StatsResponse(
            totalAmount,
            totalCount,
            averageAmount,
            byCategory,
            byType,
            periodInfo,
            evolution
        );
    }
    
    private List<CategoryStats> calculateCategoryStats(List<Expense> expenses, Double totalAmount) {
        Map<Long, List<Expense>> byCategory = expenses.stream()
            .collect(Collectors.groupingBy(
                e -> e.getCategory() != null ? e.getCategory().getId() : -1L
            ));
        
        List<CategoryStats> categoryStats = new ArrayList<>();
        
        for (Map.Entry<Long, List<Expense>> entry : byCategory.entrySet()) {
            Long categoryId = entry.getKey();
            List<Expense> categoryExpenses = entry.getValue();
            
            Double categoryTotal = categoryExpenses.stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
            Long categoryCount = (long) categoryExpenses.size();
            Double percentage = totalAmount > 0 ? (categoryTotal / totalAmount) * 100 : 0.0;
            
            String categoryName;
            if (categoryId == -1L) {
                categoryName = "Uncategorized";
            } else {
                Category category = categoryExpenses.get(0).getCategory();
                categoryName = category != null ? category.getName() : "Unknown";
            }
            
            categoryStats.add(new CategoryStats(
                categoryId == -1L ? null : categoryId,
                categoryName,
                categoryTotal,
                categoryCount,
                percentage
            ));
        }
        
        // Trier par montant décroissant
        categoryStats.sort(Comparator.comparing(CategoryStats::totalAmount).reversed());
        
        return categoryStats;
    }
    
    private List<TypeStats> calculateTypeStats(List<Expense> expenses, Double totalAmount) {
        Map<ExpenseType, List<Expense>> byType = expenses.stream()
            .filter(e -> e.getType() != null)
            .collect(Collectors.groupingBy(Expense::getType));
        
        List<TypeStats> typeStats = new ArrayList<>();
        
        for (Map.Entry<ExpenseType, List<Expense>> entry : byType.entrySet()) {
            ExpenseType type = entry.getKey();
            List<Expense> typeExpenses = entry.getValue();
            
            Double typeTotal = typeExpenses.stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
            Long typeCount = (long) typeExpenses.size();
            Double percentage = totalAmount > 0 ? (typeTotal / totalAmount) * 100 : 0.0;
            
            typeStats.add(new TypeStats(type, typeTotal, typeCount, percentage));
        }
        
        // Trier par montant décroissant
        typeStats.sort(Comparator.comparing(TypeStats::totalAmount).reversed());
        
        return typeStats;
    }
    
    private EvolutionStats calculateEvolution(LocalDate startDate, LocalDate endDate, Double currentTotal, StatsFilter filter) {
        // Calculer la durée de la période
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Calculer la période précédente
        LocalDate previousStartDate = startDate.minusDays(daysBetween);
        LocalDate previousEndDate = startDate.minusDays(1);
        
        // Récupérer les dépenses de la période précédente
        User currentUser = getCurrentUser();
        Instant previousStartInstant = previousStartDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant previousEndInstant = previousEndDate.atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC);
        
        List<Expense> previousExpenses = expenseRepository.findAllByUserAndCreatedAtBetween(
            currentUser, previousStartInstant, previousEndInstant
        );
        
        // Appliquer les mêmes filtres
        if (filter != null) {
            if (filter.type() != null && !filter.type().isBlank()) {
                ExpenseType expenseType = ExpenseType.valueOf(filter.type());
                previousExpenses = previousExpenses.stream()
                    .filter(e -> e.getType() == expenseType)
                    .collect(Collectors.toList());
            }
            
            if (filter.category_id() != null) {
                previousExpenses = previousExpenses.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().getId().equals(filter.category_id()))
                    .collect(Collectors.toList());
            }
        }
        
        Double previousTotal = previousExpenses.stream()
            .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
            .sum();
        
        // Calculer l'évolution
        Double changeAmount = currentTotal - previousTotal;
        Double changePercentage = previousTotal > 0 ? (changeAmount / previousTotal) * 100 : (currentTotal > 0 ? 100.0 : 0.0);
        
        return new EvolutionStats(changePercentage, changeAmount);
    }
    
    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

