/*******************************************************************************
 * Copyright Technophobia Ltd 2012
 * 
 * This file is part of the Substeps Eclipse Plugin.
 * 
 * The Substeps Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the Eclipse Public License v1.0.
 * 
 * The Substeps Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Eclipse Public License for more details.
 * 
 * You should have received a copy of the Eclipse Public License
 * along with the Substeps Eclipse Plugin.  If not, see <http://www.eclipse.org/legal/epl-v10.html>.
 ******************************************************************************/
package com.technophobia.substeps.junit.ui;

public enum SubstepsControlsIcon implements SubstepsIcon {

    StopEnabled("elcl16/stop.gif"), //
    StopDisbled("dlcl16/stop.gif"), //
    RelaunchEnabled("elcl16/relaunch.gif"), //
    RelaunchDisabled("dlcl16/relaunch.gif"), //
    RelaunchFailedEnabled("elcl16/relaunchf.gif"), //
    RelaunchFailedDisabled("dlcl16/relaunchf.gif"), //
    StackFilterEnabled("elcl16/cfilter.gif"), //
    StackFilterDisabled("dlcl16/cfilter.gif"), //
    ScrollLockEnabled("elcl16/lock.gif"), //
    ScrollLockDisabled("dlcl16/lock.gif"), //
    OrientationHorizontal("elcl16/th_horizontal.gif"), //
    OrientationVertical("elcl16/th_vertical.gif"), //
    OrientationAutomatic("elcl16/th_automatic.gif"), //
    CompareEnabled("elcl16/compare.gif"), //
    CompareDisabled("dlcl16/compare.gif"), //
    HierarchicalLayout("elcl16/hierarchicalLayout.gif"), //
    HistoryListEnabled("elcl16/history_list.gif"), //
    HistoryListDisabled("dlcl16/history_list.gif"), //
    SelectPreviousTestEnabled("elcl16/select_prev.gif"), //
    SelectPreviousTestDisabled("dlcl16/select_prev.gif"), //
    SelectNextTestEnabled("elcl16/select_next.gif"), //
    SelectNextTestDisabled("dlcl16/select_next.gif");

    private final String path;


    private SubstepsControlsIcon(final String path) {
        this.path = path;
    }


    @Override
    public String getPath() {
        return path;
    }
}
