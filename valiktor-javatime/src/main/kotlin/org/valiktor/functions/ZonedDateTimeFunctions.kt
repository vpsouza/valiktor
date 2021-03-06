/*
 * Copyright 2018 https://www.valiktor.org
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

package org.valiktor.functions

import org.valiktor.Validator
import org.valiktor.constraints.NotToday
import org.valiktor.constraints.Today
import java.time.LocalDate
import java.time.ZonedDateTime

/**
 * Validates if the [ZonedDateTime] property is today
 *
 * @receiver the property to be validated
 * @return the same receiver property
 */
fun <E> Validator<E>.Property<ZonedDateTime?>.isToday(): Validator<E>.Property<ZonedDateTime?> =
    this.validate(Today) { it == null || it.toLocalDate() == LocalDate.now(it.zone) }

/**
 * Validates if the [ZonedDateTime] property isn't today
 *
 * @receiver the property to be validated
 * @return the same receiver property
 */
fun <E> Validator<E>.Property<ZonedDateTime?>.isNotToday(): Validator<E>.Property<ZonedDateTime?> =
    this.validate(NotToday) { it == null || it.toLocalDate() != LocalDate.now(it.zone) }