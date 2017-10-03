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

package com.lwh.jackknife.orm.table;

import com.lwh.jackknife.orm.annotation.Column;
import com.lwh.jackknife.orm.annotation.Table;

/**
 * 要映射的实体类需要实现此接口，{@link Table}和{@link Column}可以不配置，但此接口一定要实现。
 */
public interface OrmTable {
}
