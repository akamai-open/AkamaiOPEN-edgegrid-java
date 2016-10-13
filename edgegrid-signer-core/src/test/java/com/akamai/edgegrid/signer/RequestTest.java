/*
 * Copyright 2016 Copyright 2016 Akamai Technologies, Inc. All Rights Reserved.
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

package com.akamai.edgegrid.signer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

/**
 * Unit tests for {@link Request}.
 *
 * @author mmeyer@akamai.com
 */
public class RequestTest {

    @Test(dataProvider = "absoluteUriTestData")
    public void testAcceptRequestWithAbsoluteUriAsString(
            String caseName,
            String uri,
            String expectedPath,
            String expectedQuery) throws RequestSigningException {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(uri)
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri().getPath(), equalTo(expectedPath));
        assertThat(request.getUri().getQuery(), equalTo(expectedQuery));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test(dataProvider = "absoluteUriTestData")
    public void testAcceptRequestWithAbsoluteUriAsURI(
            String caseName,
            String uri,
            String expectedPath,
            String expectedQuery) throws RequestSigningException {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create(uri))
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri().getPath(), equalTo(expectedPath));
        assertThat(request.getUri().getQuery(), equalTo(expectedQuery));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test
    public void testAcceptRequestWithRelativeUri() throws RequestSigningException {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create("/check"))
                .header("header", "h")
                .build();

        assertThat(request.getBody(), equalTo("body".getBytes()));
        assertThat(request.getMethod(), equalTo("GET"));
        assertThat(request.getUri(), equalTo(URI.create("/check")));
        assertThat(request.getHeaders().size(), equalTo(1));
        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test
    public void testHeadersLowercasing() {
        Request request = Request.builder()
                .body("body".getBytes())
                .method("GET")
                .uri(URI.create("/check"))
                .header("HeaDer", "h")
                .build();

        assertThat(request.getHeaders().get("header"), equalTo("h"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNames() {
        Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("Duplicate", "Y")
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateCaseInsensitiveHeaderNames() {
        Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("DUPLICATE", "Y")
                .build();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNamesMap() {
        Request.RequestBuilder builder = Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X");
        Map<String, String> headers = new HashMap<>();
        headers.put("Duplicate", "y");
        builder.headers(headers);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testRejectDuplicateHeaderNamesMixedCase() {
        Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .header("Duplicate", "X")
                .header("DUPLICATE", "Y")
                .build();
    }



}
