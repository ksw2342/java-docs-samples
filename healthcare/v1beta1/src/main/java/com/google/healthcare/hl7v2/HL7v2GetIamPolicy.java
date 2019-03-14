/*
 * Copyright 2019 Google LLC
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

package com.google.healthcare.hl7v2;

// [START healthcare_hl7v2_store_get_iam_policy]

import com.google.HealthcareQuickstart;
import com.google.api.services.healthcare.v1beta1.model.Policy;
import com.google.gson.Gson;

import java.io.IOException;

public class HL7v2GetIamPolicy {
  private static final Gson GSON = new Gson();

  public static void getIamPolicy(String hl7v2StoreName) throws IOException {
    Policy policy = HealthcareQuickstart.getCloudHealthcareClient()
        .projects()
        .locations()
        .datasets()
        .hl7V2Stores()
        .getIamPolicy(hl7v2StoreName)
        .execute();
    System.out.println("Retrieved HL7v2 store policy: " + GSON.toJson(policy));
  }
}
// [END healthcare_hl7v2_store_get_iam_policy]