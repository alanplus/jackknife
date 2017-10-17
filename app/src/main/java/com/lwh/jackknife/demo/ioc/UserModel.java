/*
 * Copyright (C) 2017 The JackKnife Open Source Project
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

package com.lwh.jackknife.demo.ioc;

import com.lwh.jackknife.mvp.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwenhao on 2017/10/17.
 */

public class UserModel extends BaseModel<User>{

    public UserModel(Class<User> beanClass) {
        super(beanClass);
    }

    @Override
    protected List<User> initBeans() {
        List<User> users = new ArrayList<>();
        users.add(new User("Alm", 17));
        users.add(new User("Celica", 16));
        users.add(new User("Mycen", 54));
        return users;
    }

    public List<User> findNameEqualToCelica() {
        Selector selector = new Selector();
        selector.addWhereEqualTo("name", "Celica");
        try {
            return findObjects(selector);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
