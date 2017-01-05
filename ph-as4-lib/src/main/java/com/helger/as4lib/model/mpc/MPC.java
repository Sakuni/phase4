/**
 * Copyright (C) 2015-2017 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.as4lib.model.mpc;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.type.ObjectType;
import com.helger.photon.basic.object.AbstractObject;
import com.helger.photon.security.object.StubObject;

public class MPC extends AbstractObject implements IMPC
{
  public static final ObjectType OT = new ObjectType ("as4.mpc");

  public MPC (@Nonnull @Nonempty final String sID)
  {
    this (StubObject.createForCurrentUserAndID (sID));
  }

  MPC (@Nonnull final StubObject aStubObject)
  {
    super (aStubObject);
  }

  @Nonnull
  public ObjectType getObjectType ()
  {
    return OT;
  }

}
