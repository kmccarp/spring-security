/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.security.authentication;

import reactor.core.publisher.Mono;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * A {@link ReactiveAuthenticationManager} that uses a {@link ReactiveUserDetailsService}
 * to validate the provided username and password.
 *
 * @author Rob Winch
 * @author Eddú Meléndez
 * @since 5.0
 */
public class UserDetailsRepositoryReactiveAuthenticationManagerextends AbstractUserDetailsReactiveAuthenticationManager {

	private ReactiveUserDetailsService userDetailsService;

	public UserDetailsRepositoryReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
		Assert.notNull(userDetailsService, "userDetailsService cannot be null");
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected Mono<UserDetails> retrieveUser(String username) {
		return this.userDetailsService.findByUsername(username);
	}

}
