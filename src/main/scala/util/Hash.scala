/**
 * MessageDigest functions
 */
package util

import java.security.{ MessageDigest => MD }

/**
 * @author ShiZhan
 * Java MessageDigest wrapper
 */
object Hash {

  def getMD5(s: String) =
    MD.getInstance("MD5").digest(s.getBytes).map("%02x".format(_)).mkString

}