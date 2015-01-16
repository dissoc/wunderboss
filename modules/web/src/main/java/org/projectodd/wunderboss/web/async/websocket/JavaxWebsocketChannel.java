/*
 * Copyright 2014-2015 Red Hat, Inc, and individual contributors.
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

package org.projectodd.wunderboss.web.async.websocket;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

public class JavaxWebsocketChannel extends WebsocketChannelSkeleton {

    public JavaxWebsocketChannel(final OnOpen onOpen,
                                 final OnClose onClose,
                                 final OnMessage onMessage,
                                 final OnError onError) {
        super(onOpen, onClose, onMessage, onError);
    }

    @Override
    public Endpoint getEndpoint() {
        final JavaxWebsocketChannel channel = this;
        return new Endpoint() {
            @Override
            public void onOpen(final Session session, final EndpointConfig endpointConfig) {
                session.addMessageHandler(new MessageHandler.Whole<String>() {
                    @Override
                    public void onMessage(String s) {
                        notifyMessage(s);
                    }
                });
                session.addMessageHandler(new MessageHandler.Whole<byte[]>() {
                    @Override
                    public void onMessage(byte[] s) {
                        notifyMessage(s);
                    }
                });
                channel.setUnderlyingChannel(session);
                channel.notifyOpen(endpointConfig);
            }

            @Override
            public void onClose(final Session _, final CloseReason closeReason) {
                notifyClose(closeReason.getCloseCode().getCode(),
                            closeReason.getReasonPhrase());
            }

            @Override
            public void onError(final Session _, final Throwable error) {
                notifyError(error);
            }
        };
    }

    @Override
    public void setUnderlyingChannel(Object channel) {
        this.session = (Session)channel;
    }

    @Override
    public boolean isOpen() {
        return this.session != null &&
                this.session.isOpen();
    }

    @Override
    public boolean send(Object message, final boolean shouldClose) throws Exception {
        if (!isOpen()) {
            return false;
        }

        SendHandler handler = new SendHandler() {
            @Override
            public void onResult(SendResult sendResult) {
                if (sendResult.isOK()) {
                    if (shouldClose) {
                        try {
                            close();
                        } catch (IOException e) {
                            notifyError(e);
                        }
                    }
                } else {
                    notifyError(sendResult.getException());
                }
            }
        };

        if (message instanceof String) {
            this.session.getAsyncRemote().sendText((String)message, handler);
        } else if (message instanceof byte[]) {
            this.session.getAsyncRemote().sendBinary(ByteBuffer.wrap((byte[])message), handler);
        } else {
            throw Util.wrongMessageType(message.getClass());
        }


        return true;
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            this.session.close();
        }
    }

    private Session session;
}