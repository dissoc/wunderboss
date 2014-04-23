/*
 * Copyright 2014 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.wunderboss.web;

import org.projectodd.wunderboss.Component;
import org.projectodd.wunderboss.Option;

import javax.servlet.Servlet;
import java.util.Map;

public interface Web<S> extends Component {
    class CreateOption extends Option {
        public static final CreateOption HOST       = opt("host", "localhost", CreateOption.class);
        public static final CreateOption PORT       = opt("port", 8080, CreateOption.class);
        public static final CreateOption AUTO_START = opt("auto_start", true, CreateOption.class);
    }

    class RegisterOption extends Option {
        public static final RegisterOption CONTEXT_PATH = opt("context_path", "/", RegisterOption.class);
        public static final RegisterOption DESTROY      = opt("destroy", RegisterOption.class);
        public static final RegisterOption INIT         = opt("init", RegisterOption.class);
        public static final RegisterOption STATIC_DIR   = opt("static_dir", RegisterOption.class);
    }



    Web registerHandler(S handler, Map<RegisterOption, Object> opts);

    Web registerServlet(Servlet servlet, Map<RegisterOption, Object> opts);

    Web unregister(String context);

}
