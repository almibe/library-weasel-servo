/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libraryweasel.dependencymanager

import org.libraryweasel.admin.dash.DashManager
import org.libraryweasel.plugin.http.EntryPoint
import spock.lang.Shared
import spock.lang.Specification

import java.sql.DatabaseMetaData;

public class LibraryWeaselComponentRegistrarSpec extends Specification {

    @Shared
    LibraryWeaselComponentRegistrar componentRegistrar = new LibraryWeaselComponentRegistrar(null)

    def "test getting list of interfaces component has implemented"() {
        expect:
        interfaces.containsAll(results)
        interfaces.size() == results.size()

        where:
        interfaces                                            | results
        componentRegistrar.getInterfaces(FakeComponent.class) | [EntryPoint.class]
        componentRegistrar.getInterfaces(NotAComponent.class) | []
    }

    def "test getting list of services a component depends on"() {
        expect:
        services.containsAll(results)
        services.size() == results.size()

        where:
        services                                            | results
        componentRegistrar.getServices(FakeComponent.class) | [DashManager.class, DatabaseMetaData.class]
        componentRegistrar.getServices(NotAComponent.class) | []
    }

    def "test getting list of services component tracks with callbacks"() {
        expect:
        callbacks.containsAll(results)
        callbacks.size() == results.size()

        where:
        callbacks                                            | results
        componentRegistrar.getCallbacks(FakeComponent.class) | [String.class, Integer.class]
        componentRegistrar.getCallbacks(NotAComponent.class) | []
    }

    def "test check if component"() {
        expect:
        registrar == isComponent

        where:
        registrar                                                | isComponent
        componentRegistrar.checkIfComponent(FakeComponent.class) | true
        componentRegistrar.checkIfComponent(NotAComponent.class) | false
        componentRegistrar.checkIfComponent(String.class)        | false
        componentRegistrar.checkIfComponent(List.class)          | false
    }
}
