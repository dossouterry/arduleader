package com.geeksville.flight

import org.mavlink.messages.ardupilotmega.msg_mission_item
import org.mavlink.messages.MAV_CMD
import org.mavlink.messages.MAV_FRAME

/**
 * A wrapper for waypoints - to provide a higher level API
 */
case class Waypoint(val msg: msg_mission_item) {

  private val frameCodes = Map(
    MAV_FRAME.MAV_FRAME_GLOBAL -> "MSL",
    MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT -> "AGL")

  def commandStr = Waypoint.commandCodes.get(msg.command).getOrElse("cmd=" + msg.command)
  def frameStr = frameCodes.get(msg.frame).getOrElse("frame=" + msg.frame)

  def commandStr_=(s: String) {
    msg.command = Waypoint.commandToCodes(s)
  }

  def seq = msg.seq

  /// The magic home position
  def isHome = (msg.current != 2) && (msg.seq == 0)

  /// If the airplane is heading here
  def isCurrent = msg.current == 1

  def isMSL = msg.frame == MAV_FRAME.MAV_FRAME_GLOBAL

  def isCommandValid = Waypoint.commandCodes.contains(msg.command)

  def altitude = msg.z

  def location = Location(msg.x, msg.y, Some(msg.z))

  /**
   * Should we show this waypoint on the map?
   */
  def isForMap = (msg.x != 0 || msg.y != 0) && !isJump

  //
  // Accessors for particular waypoint types
  //
  def isJump = msg.command == MAV_CMD.MAV_CMD_DO_JUMP
  def jumpSequence = msg.param1.toInt
  def loiterTime = msg.param1
  def loiterTurns = msg.param1

  /**
   * Just the type of the waypoint (RTL, LoiterN, etc...) or Home (as a special case)
   */
  def typeString = {
    if (isHome)
      "Home"
    else
      commandStr
  }

  /**
   * A short description of this waypoint
   */
  def shortString = {
    msg.command match {
      case MAV_CMD.MAV_CMD_DO_JUMP => "Jump to WP #%d".format(jumpSequence)
      case MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM => "Loiter (forever)"
      case MAV_CMD.MAV_CMD_NAV_LOITER_TURNS => "Loiter (%.1f turns)".format(loiterTurns)
      case MAV_CMD.MAV_CMD_NAV_LOITER_TIME => "Loiter (%.1f seconds)".format(loiterTime)

      // FIXME - parse takeoff/land
      case _ =>
        typeString
    }
  }

  /**
   * Try to decode arguments into something understandable by a human
   */
  private def decodedArguments = {
    msg.command match {
      case MAV_CMD.MAV_CMD_DO_JUMP => Some("Jump to WP #%d".format(jumpSequence))
      case MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM => Some("forever")
      case MAV_CMD.MAV_CMD_NAV_LOITER_TURNS => Some("%.1f turns".format(loiterTurns))
      case MAV_CMD.MAV_CMD_NAV_LOITER_TIME => Some("%.1f seconds".format(loiterTime))

      // FIXME - parse takeoff/land
      case _ =>
        None
    }
  }

  /**
   * Longer descriptiong (with arguments)
   */
  def longString = shortString + ": " + argumentsString

  /**
   * The arguments as a humang readable string
   */
  def argumentsString = {
    import msg._

    val altStr = "Altitude %sm (%s)".format(z, frameStr)

    val paramsStr = decodedArguments.map(", " + _).getOrElse {
      val params = Seq(param1, param2, param3, param4)
      val hasParams = params.find(_ != 0.0f).isDefined
      if (hasParams)
        ", params=%s".format(params.mkString(","))
      else
        ""
    }

    altStr + paramsStr
  }
}

object Waypoint {
  val commandCodes = Map(
    MAV_CMD.MAV_CMD_DO_JUMP -> "Jump",
    MAV_CMD.MAV_CMD_NAV_TAKEOFF -> "Takeoff",
    MAV_CMD.MAV_CMD_NAV_WAYPOINT -> "Waypoint", // Navigate to Waypoint
    MAV_CMD.MAV_CMD_NAV_LAND -> "Land", // LAND to Waypoint
    MAV_CMD.MAV_CMD_NAV_LOITER_UNLIM -> "Loiter", // Loiter indefinitely
    MAV_CMD.MAV_CMD_NAV_LOITER_TURNS -> "LoiterN", // Loiter N Times
    MAV_CMD.MAV_CMD_NAV_LOITER_TIME -> "LoiterT",
    MAV_CMD.MAV_CMD_NAV_RETURN_TO_LAUNCH -> "RTL")

  val commandToCodes = commandCodes.map { case (k, v) => (v, k) }

  val commandNames = commandCodes.values.toArray
}