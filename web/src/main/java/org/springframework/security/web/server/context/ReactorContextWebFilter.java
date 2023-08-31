/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.security.web.server.context;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

/**
 * Uses a {@link ServerSecurityContextRepository} to provide the {@link SecurityContext}
 * to initialize the {@link ReactiveSecurityContextHolder}.
 *
 * @author Rob Winch
 * @since 5.0
 */
public class ReactorContextWebFilter implements WebFilter {

	private final ServerSecurityContextRepository repository;

	public ReactorContextWebFilter(ServerSecurityContextRepository repository) {
		Assert.notNull(repository, "repository cannot be null");
		this.repository = repository;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange).contextWrite(
				context -> context.hasKey(SecurityContext.class) ? context : withSecurityContext(context, exchange));
	}

	private Context withSecurityContext(Context mainContext, ServerWebExchange exchange) {
		return mainContext.putAll(
				this.repository.load(exchange).as(ReactiveSecurityContextHolder::withSecurityContext).readOnly());
	}

}
