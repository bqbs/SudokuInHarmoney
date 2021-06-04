package com.jclian.sudokuinharmony.slice;

import com.jclian.sudokuinharmony.ResourceTable;
import com.jclian.sudokuinharmony.SudokuComponent;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class MainAbilitySlice extends AbilitySlice {
    SudokuComponent sudokuComponent;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        sudokuComponent = (SudokuComponent) findComponentById(ResourceTable.Id_sudoku_component);
    }

    @Override
    public void onActive() {
        super.onActive();

    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
        if (sudokuComponent != null) {
            sudokuComponent.start();
        }
    }

    @Override
    protected void onBackground() {
        super.onBackground();
        if (sudokuComponent != null) {
            sudokuComponent.dump();
        }
    }
}
