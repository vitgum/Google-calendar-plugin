package com.eyelinecom.whoisd.sads2.google.calendar.web.bot.pages;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * author: Denis Enenko
 * date: 24.04.17
 */
public abstract class BotPage {

  private final int id;
  protected final ResourceBundle bundle;
  protected final boolean error;


  protected BotPage(int id, ResourceBundle bundle) {
    this(id, bundle, false);
  }

  protected BotPage(int id, ResourceBundle bundle, boolean error) {
    this.id = id;
    this.bundle = bundle;
    this.error = error;
  }

  public abstract BotPage getNextPage(String value, PageContext ctx);

  public abstract String toXml(PageContext ctx) throws IOException;

  protected static String pageStart() {
    return "<page version=\"2.0\">";
  }

  protected static String pageEnd() {
    return "</page>";
  }

  protected static String divStart() {
    return "<div>";
  }

  protected static String divEnd() {
    return "</div>";
  }

  protected static String br() {
    return "<br/>";
  }

  protected static String buttonsStart() {
    return "<navigation>";
  }

  protected static String buttonsEnd() {
    return "</navigation>";
  }

  protected String button(int num, String value, String label, Map<String, String> params, String ctxPath) throws IOException {
    Map<String, String> _params = new HashMap<>(params);
    _params.put("user_input", value);

    return "<link accesskey=\"" + num + "\" pageId=\"" + formatPageUrl(_params, ctxPath) + "\">"
        + label
        + "</link>";
  }

  protected String anyInput(Map<String, String> params, String ctxPath) throws IOException {
    return divStart()
        + "<input name=\"user_input\" value=\"\" type=\"text\" navigationId=\"submit\"/>"
        + divEnd()
        + "<navigation id=\"submit\">"
        + "<link pageId=\"" + formatPageUrl(params, ctxPath) + "\"/>"
        + "</navigation>";
  }

  private String formatPageUrl(Map<String, String> params, String ctxPath) throws IOException {
    StringBuilder sb = new StringBuilder(ctxPath + "/?pid=" + id);

    if(params == null || params.isEmpty())
      return sb.toString();

    for(Map.Entry<String, String> e : params.entrySet()) {
      sb.append("&amp;")
          .append(URLEncoder.encode(e.getKey(), "UTF-8"))
          .append("=")
          .append(URLEncoder.encode(e.getValue(), "UTF-8"));
    }

    return sb.toString();
  }

}