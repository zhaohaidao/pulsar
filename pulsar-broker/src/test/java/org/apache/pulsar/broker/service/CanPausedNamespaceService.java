/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.broker.service;

import java.util.concurrent.locks.ReentrantLock;
import org.apache.pulsar.broker.PulsarService;
import org.apache.pulsar.broker.namespace.NamespaceService;
import org.apache.pulsar.common.naming.NamespaceBundle;

public class CanPausedNamespaceService extends NamespaceService {

    private volatile boolean paused = false;

    private ReentrantLock lock = new ReentrantLock();

    public CanPausedNamespaceService(PulsarService pulsar) {
        super(pulsar);
    }

    @Override
    protected void onNamespaceBundleOwned(NamespaceBundle bundle) {
        lock.lock();
        try {
            if (paused){
                return;
            }
            super.onNamespaceBundleOwned(bundle);
        } finally {
            lock.unlock();
        }
    }

    public void pause(){
        lock.lock();
        try {
            paused = true;
        } finally {
            lock.unlock();
        }
    }

    public void resume(){
        lock.lock();
        try {
            paused = false;
        } finally {
            lock.unlock();
        }
    }
}
