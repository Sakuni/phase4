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
package com.helger.as4.server.supplementary.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.apache.wss4j.dom.str.STRParser.REFERENCE_TYPE;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.as4.crypto.AS4CryptoFactory;
import com.helger.as4.crypto.CryptoProperties;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xml.serialize.read.DOMReader;

/**
 * A set of test-cases for signing and verifying SOAP requests.
 */
public class SignatureTest
{
  private final WSSecurityEngine secEngine = new WSSecurityEngine ();
  private final Crypto m_aCrypto;
  private final AS4CryptoFactory m_aAS4CryptoFactory;
  private final CryptoProperties m_aCryptoProperties;

  public SignatureTest () throws Exception
  {
    m_aAS4CryptoFactory = new AS4CryptoFactory ();
    m_aCrypto = m_aAS4CryptoFactory.getCrypto ();
    m_aCryptoProperties = m_aAS4CryptoFactory.getCryptoProperties ();
  }

  /**
   * The test uses the Issuer Serial key identifier type.
   * <p/>
   *
   * @throws java.lang.Exception
   *         Thrown when there is any problem in signing or verification
   */
  @Test
  public void testX509SignatureIS () throws Exception
  {
    final WSSecSignature builder = new WSSecSignature ();
    builder.setUserInfo (m_aCryptoProperties.getKeyAlias (), m_aCryptoProperties.getKeyPassword ());
    builder.setKeyIdentifierType (WSConstants.BST_DIRECT_REFERENCE);
    builder.setSignatureAlgorithm ("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    // PMode indicates the DigestAlgorithmen as Hash Function
    builder.setDigestAlgo ("http://www.w3.org/2001/04/xmlenc#sha256");
    final Document doc = _getSoapEnvelope11 ();
    final WSSecHeader secHeader = new WSSecHeader (doc);
    secHeader.insertSecurityHeader ();
    final Document signedDoc = builder.build (doc, m_aCrypto, secHeader);

    // final String outputString = XMLUtils.prettyDocumentToString (signedDoc);

    final WSHandlerResult results = verify (signedDoc);

    final WSSecurityEngineResult actionResult = results.getActionResults ()
                                                       .get (Integer.valueOf (WSConstants.SIGN))
                                                       .get (0);
    assertNotNull (actionResult.get (WSSecurityEngineResult.TAG_X509_CERTIFICATE));
    assertNotNull (actionResult.get (WSSecurityEngineResult.TAG_X509_REFERENCE_TYPE));
    final REFERENCE_TYPE referenceType = (REFERENCE_TYPE) actionResult.get (WSSecurityEngineResult.TAG_X509_REFERENCE_TYPE);
    assertSame (referenceType, REFERENCE_TYPE.DIRECT_REF);
  }

  /**
   * Verifies the soap envelope. This method verifies all the signature
   * generated.
   *
   * @param env
   *        soap envelope
   * @throws java.lang.Exception
   *         Thrown when there is a problem in verification
   */
  private WSHandlerResult verify (final Document doc) throws Exception
  {
    return secEngine.processSecurityHeader (doc, null, null, m_aCrypto);
  }

  private Document _getSoapEnvelope11 () throws SAXException
  {
    return DOMReader.readXMLDOM (new ClassPathResource ("UserMessageWithoutWSSE.xml"));
  }
}