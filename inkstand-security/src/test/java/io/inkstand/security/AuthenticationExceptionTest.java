/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
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

package io.inkstand.security;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Gerald on 23.07.2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationExceptionTest {

    @Mock
    private Throwable cause;

    /**
     * The class under test
     */
    private AuthenticationException subject_with_userId;
    private AuthenticationException subject_with_userId_and_cause;
    private AuthenticationException subject_with_message_and_userId_and_cause;
    private AuthenticationException subject_with_message_and_userId;

    @Before
    public void setUp() throws Exception {
        subject_with_userId = new AuthenticationException("testUser");
        subject_with_userId_and_cause = new AuthenticationException("testUser", cause);
        subject_with_message_and_userId = new AuthenticationException("myMessage", "testUser");
        subject_with_message_and_userId_and_cause = new AuthenticationException("myMessage","testUser", cause);
    }

    @Test
    public void testGetMessage() throws Exception {
        assertEquals("Authentication failed [user=testUser]", subject_with_userId.getMessage());
        assertEquals("Authentication failed [user=testUser]", subject_with_userId_and_cause.getMessage());
        assertEquals("myMessage [user=testUser]", subject_with_message_and_userId.getMessage());
        assertEquals("myMessage [user=testUser]", subject_with_message_and_userId_and_cause.getMessage());
    }

    @Test
    public void testGetLocalizedMessage() throws Exception {
        assertEquals("Authentication failed [user=testUser]", subject_with_userId.getMessage());
        assertEquals("Authentication failed [user=testUser]", subject_with_userId_and_cause.getMessage());
        assertEquals("myMessage [user=testUser]", subject_with_message_and_userId.getMessage());
        assertEquals("myMessage [user=testUser]", subject_with_message_and_userId_and_cause.getMessage());
    }

    @Test
    public void testGetUserId() throws Exception {
        assertEquals("testUser", subject_with_userId.getUserId());
        assertEquals("testUser", subject_with_userId_and_cause.getUserId());
        assertEquals("testUser", subject_with_message_and_userId.getUserId());
        assertEquals("testUser", subject_with_message_and_userId_and_cause.getUserId());
    }

    @Test
    public void testGetCause_noCause_null() throws Exception {
        assertEquals(null, subject_with_userId.getCause());
        assertEquals(null, subject_with_message_and_userId.getCause());
    }
    @Test
    public void testGetCause_witCause() throws Exception {
        assertEquals(cause, subject_with_userId_and_cause.getCause());
        assertEquals(cause, subject_with_message_and_userId_and_cause.getCause());
    }
}
