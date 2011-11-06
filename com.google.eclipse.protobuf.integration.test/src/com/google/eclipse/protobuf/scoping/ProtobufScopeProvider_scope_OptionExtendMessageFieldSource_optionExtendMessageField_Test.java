/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.core.Setups.integrationTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.scoping.ContainAllNames.containAll;
import static com.google.eclipse.protobuf.scoping.IEObjectDescriptions.descriptionsIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufScopeProvider#scope_OptionExtendMessageFieldSource_optionExtendMessageField(OptionExtendMessageFieldSource, EReference)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufScopeProvider_scope_OptionExtendMessageFieldSource_optionExtendMessageField_Test {

  private static EReference reference;

  @BeforeClass public static void setUpOnce() {
    reference = mock(EReference.class);
  }

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private ProtobufScopeProvider provider;

  @Before public void setUp() {
    provider = xtext.getInstanceOf(ProtobufScopeProvider.class);
  }

  // package com.google.proto;
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   optional double code = 1;
  //   optional string name = 2;
  //   extensions 10 to max;
  // }
  //
  // extend Type {
  //   optional boolean active = 10;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Type type = 1000;
  // }
  //
  // option (type).(active) = true;
  @Test public void should_provide_extend_message_fields_for_first_field_in_custom_option() {
    CustomOption option = xtext.find("type", ")", CustomOption.class);
    OptionExtendMessageFieldSource codeOptionField = (OptionExtendMessageFieldSource) option.getOptionFields().get(0);
    IScope scope = provider.scope_OptionExtendMessageFieldSource_optionExtendMessageField(codeOptionField, reference);
    assertThat(descriptionsIn(scope), containAll("active", "com.google.proto.active", ".com.google.proto.active"));
  }

  // package com.google.proto;
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Type {
  //   optional double code = 1;
  //   optional string name = 2;
  //   extensions 10 to max;
  // }
  //
  // extend Type {
  //   optional boolean active = 10;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Type type = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(type).(active) = true];
  // }
  @Test public void should_provide_message_fields_for_first_field_in_field_custom_option() {
    CustomFieldOption option = xtext.find("type", ")", CustomFieldOption.class);
    OptionExtendMessageFieldSource codeOptionField = (OptionExtendMessageFieldSource) option.getOptionFields().get(0);
    IScope scope = provider.scope_OptionExtendMessageFieldSource_optionExtendMessageField(codeOptionField, reference);
    assertThat(descriptionsIn(scope), containAll("active", "com.google.proto.active", ".com.google.proto.active"));
  }
}
