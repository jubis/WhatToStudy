package utils

import com.fasterxml.jackson.databind.JsonNode

object JsonUtils {

  def getText(node: JsonNode, field: String): String = {
    node.get(field).asText()
  }

}
