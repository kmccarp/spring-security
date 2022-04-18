/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.web.util.matcher;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RequestMatcherEntryTests {

	@Test
	public void constructWhenGetRequestMatcherAndEntryThenSameRequestMatcherAndEntry() {
		RequestMatcher requestMatcher = mock(RequestMatcher.class);
		RequestMatcherEntry<String> entry = new RequestMatcherEntry<>(requestMatcher, "entry");
		assertThat(entry.getRequestMatcher()).isSameAs(requestMatcher);
		assertThat(entry.getEntry()).isEqualTo("entry");
	}

	@Test
	public void constructWhenNullValuesThenNullValues() {
		RequestMatcherEntry<String> entry = new RequestMatcherEntry<>(null, null);
		assertThat(entry.getRequestMatcher()).isNull();
		assertThat(entry.getEntry()).isNull();
	}

}