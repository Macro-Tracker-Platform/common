package com.olehprukhnytskyi.model;

import com.olehprukhnytskyi.util.UnitType;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public interface NutrientAware {
    BigDecimal getCalories();

    BigDecimal getProtein();

    BigDecimal getFat();

    BigDecimal getCarbohydrates();

    BigDecimal getCaloriesPerPiece();

    BigDecimal getProteinPerPiece();

    BigDecimal getFatPerPiece();

    BigDecimal getCarbohydratesPerPiece();

    default Set<UnitType> getAvailableUnits() {
        Set<UnitType> units = new HashSet<>();
        if (isGramsDataComplete()) {
            units.add(UnitType.GRAMS);
        }
        if (isPiecesDataComplete()) {
            units.add(UnitType.PIECES);
        }
        if (units.isEmpty()) {
            units.add(UnitType.GRAMS);
        }
        return units;
    }

    default boolean isGramsDataComplete() {
        return getCalories() != null && getProtein() != null
               && getFat() != null && getCarbohydrates() != null;
    }

    default boolean isPiecesDataComplete() {
        return getCaloriesPerPiece() != null && getProteinPerPiece() != null
               && getFatPerPiece() != null && getCarbohydratesPerPiece() != null;
    }
}
