/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package com.codenvy.qa;

import java.io.Serializable;

public interface Employee extends Serializable {

    public double getAvrSalary();
    public String toString();
    public String getEmployeeNumber();
    public String getSurname();
    public String getDateOfBirth();
}
