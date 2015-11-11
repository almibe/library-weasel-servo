/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libraryweasel.dependencymanager;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import org.libraryweasel.admin.dash.Dash;
import org.libraryweasel.admin.dash.DashManager;
import org.libraryweasel.plugin.http.EntryPoint;

import java.sql.DatabaseMetaData;

@Component(EntryPoint.class)
@Callback(String.class)
@Callback(Integer.class)
public class FakeComponent implements EntryPoint, Dash {

    @Service
    private DashManager dashManager;

    @Service
    private DatabaseMetaData databaseMetaData;

    private void addString(String s) {}
    private void removeString(String s) {}
    private void addInteger(Integer i) {}
    private void removeInteger(Integer i) {}

    @Override
    public Node getDash() {
        return null;
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public String getKit() {
        return null;
    }

    @Override
    public float getPosition() {
        return 0;
    }
}
