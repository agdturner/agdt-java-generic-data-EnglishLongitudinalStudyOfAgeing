/*
 * Copyright 2018 geoagdt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.andyt.generic.data.elsa.io;

import java.io.File;
import uk.ac.leeds.ccg.andyt.data.io.Data_Files;
import uk.ac.leeds.ccg.andyt.generic.data.elsa.core.ELSA_Strings;

/**
 *
 * @author geoagdt
 */
public class ELSA_Files extends Data_Files {

    /**
     *
     * @param dataDir
     */
    public ELSA_Files(File dataDir) {
        super(dataDir);
    }

    public File getInputELSADir() {
        File r = new File(getInputDataDir(), ELSA_Strings.s_ELSA);
        r = new File(r, "UKDA-5050-tab");
        r = new File(r, "tab");
        return r;
    }

    public File getGeneratedELSADir() {
        File r  = new File(getGeneratedDataDir(), ELSA_Strings.s_ELSA);
        r.mkdirs();
        return r;
    }
    
    public File getGeneratedELSASubsetsDir() {
        File f = new File(getGeneratedELSADir(), ELSA_Strings.s_Subsets);
        f.mkdirs();
        return f;
    }
}
