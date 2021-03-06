/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.indices.store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.plugins.IndexStorePlugin;

import java.nio.file.Path;
import java.util.List;

public class CompositeIndexFoldersDeletionListener implements IndexStorePlugin.IndexFoldersDeletionListener {

    private static final Logger logger = LogManager.getLogger(CompositeIndexFoldersDeletionListener.class);
    private final List<IndexStorePlugin.IndexFoldersDeletionListener> listeners;

    public CompositeIndexFoldersDeletionListener(List<IndexStorePlugin.IndexFoldersDeletionListener> listeners) {
        for (IndexStorePlugin.IndexFoldersDeletionListener listener : listeners) {
            if (listener == null) {
                throw new IllegalArgumentException("listeners must be non-null");
            }
        }
        this.listeners = List.copyOf(listeners);
    }

    @Override
    public void beforeIndexFoldersDeleted(Index index, IndexSettings indexSettings, Path[] indexPaths) {
        for (IndexStorePlugin.IndexFoldersDeletionListener listener : listeners) {
            try {
                listener.beforeIndexFoldersDeleted(index, indexSettings, indexPaths);
            } catch (Exception e) {
                assert false : new AssertionError(e);
                throw e;
            }
        }
    }

    @Override
    public void beforeShardFoldersDeleted(ShardId shardId, IndexSettings indexSettings, Path[] shardPaths) {
        for (IndexStorePlugin.IndexFoldersDeletionListener listener : listeners) {
            try {
                listener.beforeShardFoldersDeleted(shardId, indexSettings, shardPaths);
            } catch (Exception e) {
                assert false : new AssertionError(e);
                throw e;
            }
        }
    }
}
