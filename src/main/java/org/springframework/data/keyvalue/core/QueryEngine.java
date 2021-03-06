/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.data.keyvalue.core;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.data.keyvalue.core.query.KeyValueQuery;

/**
 * Base implementation for accessing and executing {@link KeyValueQuery} against a {@link KeyValueAdapter}.
 * 
 * @author Christoph Strobl
 * @param <ADAPTER>
 * @param <CRITERIA>
 * @param <SORT>
 */
public abstract class QueryEngine<ADAPTER extends KeyValueAdapter, CRITERIA, SORT> {

	private final CriteriaAccessor<CRITERIA> criteriaAccessor;
	private final SortAccessor<SORT> sortAccessor;

	private ADAPTER adapter;

	public QueryEngine(CriteriaAccessor<CRITERIA> criteriaAccessor, SortAccessor<SORT> sortAccessor) {

		this.criteriaAccessor = criteriaAccessor;
		this.sortAccessor = sortAccessor;
	}

	/**
	 * Extract query attributes and delegate to concrete execution.
	 * 
	 * @param query
	 * @param keyspace
	 * @return
	 */
	public Collection<?> execute(KeyValueQuery<?> query, Serializable keyspace) {

		CRITERIA criteria = this.criteriaAccessor != null ? this.criteriaAccessor.resolve(query) : null;
		SORT sort = this.sortAccessor != null ? this.sortAccessor.resolve(query) : null;

		return execute(criteria, sort, query.getOffset(), query.getRows(), keyspace);
	}

	/**
	 * Extract query attributes and delegate to concrete execution.
	 * 
	 * @param query
	 * @param keyspace
	 * @return
	 */
	public long count(KeyValueQuery<?> query, Serializable keyspace) {

		CRITERIA criteria = this.criteriaAccessor != null ? this.criteriaAccessor.resolve(query) : null;
		return count(criteria, keyspace);
	}

	/**
	 * @param criteria
	 * @param sort
	 * @param offset
	 * @param rows
	 * @param keyspace
	 * @return
	 */
	public abstract Collection<?> execute(CRITERIA criteria, SORT sort, int offset, int rows, Serializable keyspace);

	/**
	 * @param criteria
	 * @param keyspace
	 * @return
	 */
	public abstract long count(CRITERIA criteria, Serializable keyspace);

	/**
	 * Get the {@link KeyValueAdapter} used.
	 * 
	 * @return
	 */
	protected ADAPTER getAdapter() {
		return this.adapter;
	}

	/**
	 * @param adapter
	 */
	@SuppressWarnings("unchecked")
	public void registerAdapter(KeyValueAdapter adapter) {

		if (this.adapter == null) {
			this.adapter = (ADAPTER) adapter;
		} else {
			throw new IllegalArgumentException("Cannot register more than one adapter for this QueryEngine.");
		}
	}
}
