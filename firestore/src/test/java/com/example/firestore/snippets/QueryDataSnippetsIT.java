/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.firestore.snippets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class QueryDataSnippetsIT {

  private static Firestore db;
  private static QueryDataSnippets queryDataSnippets;
  private static String projectId = "java-docs-samples-firestore";

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId(projectId)
            .build();
    db = firestoreOptions.getService();
    deleteAllDocuments();
    queryDataSnippets = new QueryDataSnippets(db);
    queryDataSnippets.prepareExamples();
  }

  @Test
  public void testCreateAQuery() throws Exception {
    Query q = queryDataSnippets.createAQuery();
    Set<String> result = getResultsAsSet(q);
    Set<String> expectedResults = new HashSet<>(Arrays.asList("DC", "TOK", "BJ"));
    assertTrue(Objects.equals(result, expectedResults));
  }

  @Test
  public void testSimpleQueryReturnsExpectedResults() throws Exception {
    List<Set<String>> expectedResults = new ArrayList<>();

    expectedResults.add(new HashSet<>(Arrays.asList("SF", "LA")));
    expectedResults.add(new HashSet<>(Arrays.asList("SF", "DC")));
    expectedResults.add(new HashSet<>(Arrays.asList("SF", "DC", "TOK")));

    List<Query> queries = queryDataSnippets.createSimpleQueries();
    for (int i = 0; i < queries.size(); i++) {
      Set<String> results = getResultsAsSet(queries.get(i));
      assertTrue(Objects.equals(results, expectedResults.get(i)));
    }
  }

  @Test
  public void testChainedQuery() throws Exception {
    Query q = queryDataSnippets.createChainedQuery();
    Set<String> result = getResultsAsSet(q);
    Set<String> expectedResults = new HashSet<>();
    assertTrue(Objects.equals(result, expectedResults));
  }

  @Test
  public void testRangeQuery() throws Exception {
    Query q = queryDataSnippets.createRangeQuery();
    Set<String> result = getResultsAsSet(q);
    Set<String> expectedResults = new HashSet<>(Arrays.asList("SF", "LA"));
    assertTrue(Objects.equals(result, expectedResults));
  }

  @Test(expected = Exception.class)
  public void testInvalidRangeQueryThrowsException() throws Exception {
    Query q = queryDataSnippets.createInvalidRangeQuery();
    getResults(q);
  }

  @Test
  public void testOrderByNameWithLimitQuery() throws Exception {
    Query q = queryDataSnippets.createOrderByNameWithLimitQuery();
    List<String> result = getResults(q);
    List<String> expectedResults = Arrays.asList("BJ", "LA", "SF");
    assertEquals(result, expectedResults);
  }

  @Test
  public void testOrderByNameDescWithLimitQuery() throws Exception {
    Query q = queryDataSnippets.createOrderByNameDescWithLimitQuery();
    List<String> result = getResults(q);
    List<String> expectedResults = Arrays.asList("DC", "TOK", "SF");
    assertTrue(Objects.equals(result, expectedResults));
  }

  @Test
  public void testWhereWithOrderByAndLimitQuery() throws Exception {
    Query q = queryDataSnippets.createWhereWithOrderByAndLimitQuery();
    List<String> result = getResults(q);
    List<String> expectedResults = Arrays.asList("LA", "TOK");
    assertEquals(result, expectedResults);
  }

  @Test
  public void testRangeWithOrderByQuery() throws Exception {
    Query q = queryDataSnippets.createRangeWithOrderByQuery();
    List<String> result = getResults(q);
    List<String> expectedResults = Arrays.asList("LA", "TOK", "BJ");
    assertEquals(result, expectedResults);
  }

  @Test(expected = Exception.class)
  public void testInvalidRangeWithOrderByQuery() throws Exception {
    Query q = queryDataSnippets.createInvalidRangeWithOrderByQuery();
    getResults(q);
  }

  @Test
  public void testStartAtFieldQueryCursor() throws Exception {
    Query q = queryDataSnippets.createStartAtFieldQueryCursor();
    List<String> expectedResults = Arrays.asList("TOK", "BJ");
    List<String> result = getResults(q);
    assertTrue(Objects.equals(result, expectedResults));
  }

  @Test
  public void testEndAtFieldQueryCursor() throws Exception {
    Query q = queryDataSnippets.createEndAtFieldQueryCursor();
    List<String> expectedResults = Arrays.asList("DC", "SF", "LA");
    List<String> result = getResults(q);
    assertEquals(result, expectedResults);
  }

  @Test
  public void testMultipleCursorConditions() throws Exception {
    // populate us_cities collection
    Map<String, String> city1 = new ImmutableMap.Builder<String, String>()
        .put("name", "Springfield").put("state", "Massachusetts").build();
    Map<String, String> city2 = new ImmutableMap.Builder<String, String>()
        .put("name", "Springfield").put("state", "Missouri").build();
    Map<String, String> city3 = new ImmutableMap.Builder<String, String>()
        .put("name", "Springfield").put("state", "Wisconsin").build();

    db.collection("us_cities").document("Massachusetts").set(city1).get();
    db.collection("us_cities").document("Missouri").set(city2).get();
    db.collection("us_cities").document("Wisconsin").set(city3).get();

    Query query1 = db.collection("us_cities")
        .orderBy("name")
        .orderBy("state")
        .startAt("Springfield");

    // all documents are retrieved
    QuerySnapshot querySnapshot = query1.get().get();
    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
    assertEquals(3, docs.size());


    // Will return "Springfield, Missouri" and "Springfield, Wisconsin"
    Query query2 = db.collection("us_cities")
        .orderBy("name")
        .orderBy("state")
        .startAt("Springfield", "Missouri");

    // only Missouri and Wisconsin are retrieved
    List<String> expectedResults = Arrays.asList("Missouri", "Wisconsin");
    List<String> result = getResults(query2);
    assertTrue(Objects.equals(result, expectedResults));
  }

  private Set<String> getResultsAsSet(Query query) throws Exception {
    List<String> docIds = getResults(query);
    return new HashSet<>(docIds);
  }

  private List<String> getResults(Query query) throws Exception {
    // asynchronously retrieve query results
    ApiFuture<QuerySnapshot> future = query.get();
    // block on response
    QuerySnapshot querySnapshot = future.get();
    List<String> docIds = new ArrayList<>();
    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
      docIds.add(document.getId());
    }
    return docIds;
  }

  private static void deleteAllDocuments() throws Exception {
    ApiFuture<QuerySnapshot> future = db.collection("cities").get();
    QuerySnapshot querySnapshot = future.get();
    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
      // block on delete operation
      db.collection("cities").document(doc.getId()).delete().get();
    }
  }

  @AfterClass
  public static void tearDown() throws Exception {
    deleteAllDocuments();
  }
}