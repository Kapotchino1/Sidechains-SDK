package com.horizen.companion

import org.scalatest.junit.JUnitSuite

import org.junit.Test
import org.junit.Assert._

import com.horizen.fixtures._
import com.horizen.customtypes._
import com.horizen.box._
import com.horizen.proposition._

import java.util.{HashMap => JHashMap}
import java.lang.{Byte => JByte}

class SidechainBoxesCompanionTest
  extends JUnitSuite
    with BoxFixture
{

  var customBoxesSerializers: JHashMap[JByte, BoxSerializer[_ <: Box[_ <: Proposition]]] = new JHashMap()
  customBoxesSerializers.put(CustomBox.BOX_TYPE_ID, CustomBoxSerializer.getSerializer)

  val sidechainBoxesCompanion = SidechainBoxesCompanion(customBoxesSerializers)
  val sidechainBoxesCompanionCore = SidechainBoxesCompanion(new JHashMap())

  @Test def testCore(): Unit = {
    // Test 1: RegularBox serialization/deserialization
    val regulerBox = getRegularBox()

    val regularBoxBytes = sidechainBoxesCompanion.toBytes(regulerBox)

    assertEquals("Type of serialized box must be RegularBox.", regulerBox.boxTypeId(), regularBoxBytes(0))
    assertEquals("Deserialization must restore same box.", regulerBox, sidechainBoxesCompanion.parseBytes(regularBoxBytes).get)


    // Test 2: CertifierRightBox serialization/deserialization
    val certifiedRightBox = getCertifierRightBox()

    val certifiedRightBoxBytes = sidechainBoxesCompanion.toBytes(certifiedRightBox)

    assertEquals("Type of serialized box must be CertifierRightBox.", certifiedRightBox.boxTypeId(), certifiedRightBoxBytes(0))
    assertEquals("Deserialization must restore same box.", certifiedRightBox, sidechainBoxesCompanion.parseBytes(certifiedRightBoxBytes).get)
  }

  @Test def testRegisteredCustom(): Unit = {
    val customBox = getCustomBox()

    val customBoxBytes = sidechainBoxesCompanion.toBytes(customBox)
    assertEquals("Box type must be custom.", Byte.MaxValue, customBoxBytes(0))
    assertEquals("Type of serialized box must be CustomBox.", customBox.boxTypeId(), customBoxBytes(1))
    assertEquals("Deserialization must restore same box.", customBox, sidechainBoxesCompanion.parseBytes(customBoxBytes).get)
  }

  @Test def testUnregisteredCustom(): Unit = {
    val customBox = getCustomBox()
    var exceptionThrown = false


    // Test 1: try to serialize custom type Box. Serialization exception expected, because of custom type is unregistered.
    try {
      sidechainBoxesCompanionCore.toBytes(customBox)
    } catch {
      case _ : Throwable => exceptionThrown = true
    }

    assertTrue("Exception must be thrown for unregistered box type.", exceptionThrown)


    // Test 2: try to deserialize custom type Box. Serialization exception expected, because of custom type is unregistered.
    exceptionThrown = false
    val customBoxBytes = sidechainBoxesCompanion.toBytes(customBox)

    try {
      sidechainBoxesCompanionCore.parseBytes(customBoxBytes).get
    } catch {
      case _ : Throwable => exceptionThrown = true
    }

    assertTrue("Exception must be thrown for unregistered box type.", exceptionThrown)
  }
}