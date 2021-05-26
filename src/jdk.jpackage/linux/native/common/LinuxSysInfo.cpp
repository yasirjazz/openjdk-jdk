/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include <limits.h>
#include <unistd.h>
#include "UnixSysInfo.h"
#include "FileUtils.h"
#include "ErrorHandling.h"

namespace SysInfo {

tstring getProcessModulePath() {
    const char* path = "/proc/self/exe";
    char buffer[PATH_MAX] = { 0 };
    ssize_t len = readlink(path, buffer, sizeof(buffer));
    if (len < 0) {
        JP_THROW(tstrings::any() << "readlink(" << path
                << ") failed. Error: " << lastCRTError());
    }

    return tstring(buffer, len);
}

tstring_array getCommandArgs(CommandArgProgramNameMode progNameMode) {
    tstring_array result;
    for (int i = progNameMode == ExcludeProgramName ? 1 : 0; i < argc; i++) {
        result.push_back(argv[i]);
    }
    return result;
}

tstring getLibPath() {
    return getEnvVariable(_T("LD_LIBRARY_PATH"));
}

} // end of namespace SysInfo
