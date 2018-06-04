/**
 * Copyright © 2018 Mercateo AG (http://www.mercateo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mercateo.eventstore.data;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mercateo.eventstore.domain.Reference;
import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
@JsonSerialize(as = ImmutableReferenceData.class)
@JsonDeserialize(as = ImmutableReferenceData.class)
public interface ReferenceData {

    static ReferenceData of(Reference reference) {
        return ImmutableReferenceData.builder().id(reference.id()).type(reference.type()).build();
    }

    @NotNull
    UUID id();

    @NotNull
    String type();

}
