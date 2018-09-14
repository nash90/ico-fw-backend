package content;

public class InitializePage
{

	public static final String	INIT_MALFORMED				= "Malformed Initialization Data";
	public static final String	AUTH_FAILED					= "Authentication Failed";
	public static final String	INIT_AUTHORIZATION_FIELD	= "Authorization Code";
	public static final String	AGENT_KEY_FIELD				= "Agent Field";

	//@formatter:off
    public static final String DYNAMIC_CONTENT_LINE = "\n" + 
            "<div class='row'>\n" + 
            "    <label>{KEY}:</label>\n" + 
            "    <textarea name='{KEY}' id='{KEY}' value='' rows='4'></textarea>\n" +
            "</div>\n";
    public static final String AGENT_KEY_CONTENT = "\n" + 
            "          <div class='row'>\n" + 
            "              <label>" + AGENT_KEY_FIELD + ":</label><br>\n" + 
            "              <input id='" + AGENT_KEY_FIELD + "' name='" + AGENT_KEY_FIELD + "' type='text'>\n" + 
            "          </div>\n";
    //@formatter:on

	//@formatter:off
    public static final String INIT_PAGE_HTML = //
            "<!DOCTYPE html>\n" + 
            "<html>\n" + 
            "<head>\n" + 
            "    <title>ICO Setup:{SITEURL}</title>\n" + 
            "\n" + 
            "    <style>\n" + 
            "    /* Base and Container Styles */\n" + 
            "    * {\n" + 
            "      -webkit-box-sizing: border-box;\n" + 
            "              box-sizing: border-box;\n" + 
            "    }\n" + 
            "\n" + 
            "    body {\n" + 
            "      width: 100%;\n" + 
            "      height: auto;\n" + 
            "      padding-top: 15px;\n" + 
            "      margin: 20px auto;\n" + 
            "      background-color: #E2E5E7;\n" + 
            "    }\n" + 
            "\n" + 
            "    .form-container {\n" + 
            "      width: 50%;\n" + 
            "      height: auto;\n" + 
            "      padding-top: 10px;\n" + 
            "      padding-bottom: 30px;\n" + 
            "      margin: auto;\n" + 
            "      background-color: #F5F5F5;\n" + 
            "      border-top: 10px solid #1D6A96;\n" + 
            "      border-radius: 5px;\n" + 
            "      -webkit-box-shadow: 0px 0px 30px 2px rgba(0,0,0,0.2);\n" + 
            "      box-shadow: 0px 0px 30px 2px rgba(0,0,0,0.2);\n" + 
            "\n" + 
            "    }\n" + 
            "\n" + 
            "    /* Form Styles */\n" + 
            "\n" + 
            "    form {\n" + 
            "      width: 80%;\n" + 
            "      margin: auto;\n" + 
            "    }\n" + 
            "\n" + 
            "    h1 {\n" + 
            "      text-align: center;\n" + 
            "      color: #283b42;\n" + 
            "      font-family: sans-serif;\n" + 
            "      font-size: 28px;\n" + 
            "    }\n" + 
            "\n" + 
            "    h2 {\n" + 
            "      text-align: center;\n" + 
            "      color: #899497;\n" + 
            "      font-family: sans-serif;\n" + 
            "      font-size: 22px;\n" + 
            "    }\n" + 
            "\n" + 
            "    label {\n" + 
            "      margin-bottom: 10px;\n" + 
            "      display: inline-block;\n" + 
            "      font-family: sans-serif;\n" + 
            "      font-size: 10px;\n" + 
            "      text-transform: uppercase;\n" + 
            "      letter-spacing: 2px;\n" + 
            "      color: #3A3A3A;\n" + 
            "    }\n" + 
            "\n" + 
            "    input {\n" + 
            "      height: auto;\n" + 
            "      width: 100%;\n" + 
            "      padding: 5px 10px;\n" + 
            "      margin-bottom: 30px;\n" + 
            "      display: inline-block;\n" + 
            "      -webkit-box-sizing: border-box;\n" + 
            "              box-sizing: border-box;\n" + 
            "      font-family: monospace;\n" + 
            "      font-size: 18px;\n" + 
            "      color: #2F4F4F;\n" + 
            "      background-color: #F5F5F5;\n" + 
            "      border: none;\n" + 
            "      border-bottom: 3px solid #B9C6CD;\n" + 
            "      -webkit-transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                          background-color 0.3s ease 0.3ms;\n" + 
            "      -o-transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                     background-color 0.3s ease 0.3ms;\n" + 
            "      transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                  background-color 0.3s ease 0.3ms;\n" + 
            "    }\n" + 
            "\n" + 
            "    input:focus {\n" + 
            "      outline: none;\n" + 
            "      border-color: #1D6A96;\n" + 
            "      background-color: #fff;\n" + 
            "    }\n" + 
            "\n" + 
            "    input:active {\n" + 
            "      border-color: #1D6A96;\n" + 
            "      background-color: #fff;\n" + 
            "    }\n" + 
            "\n" + 
            "    textarea {\n" + 
            "      resize: none;\n" +
            "      height: auto;\n" + 
            "      width: 100%;\n" + 
            "      padding: 5px 10px;\n" + 
            "      margin-bottom: 30px;\n" + 
            "      display: inline-block;\n" + 
            "      -webkit-box-sizing: border-box;\n" + 
            "              box-sizing: border-box;\n" + 
            "      font-family: monospace;\n" + 
            "      font-size: 18px;\n" + 
            "      color: #2F4F4F;\n" + 
            "      background-color: #F5F5F5;\n" + 
            "      border: none;\n" + 
            "      border-bottom: 3px solid #B9C6CD;\n" + 
            "      -webkit-transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                          background-color 0.3s ease 0.3ms;\n" + 
            "      -o-transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                     background-color 0.3s ease 0.3ms;\n" + 
            "      transition: border-bottom 0.3s ease 0.3ms, \n" + 
            "                  background-color 0.3s ease 0.3ms;\n" + 
            "    }\n" + 
            "\n" +
            "    textarea:focus {\n" + 
            "      outline: none;\n" + 
            "      border-color: #1D6A96;\n" + 
            "      background-color: #fff;\n" + 
            "    }\n" + 
            "\n" +
            "    textarea:active {\n" + 
            "      border-color: #1D6A96;\n" + 
            "      background-color: #fff;\n" + 
            "    }\n" + 
            "\n" + 
            "    .row {\n" + 
            "      margin: auto;\n" + 
            "    }\n" + 
            "\n" + 
            "    .row-head {\n" + 
            "      margin: 30px auto;\n" + 
            "      padding-bottom: 30px;\n" + 
            "      border-bottom: 1px solid #DEDEDE;\n" + 
            "    }\n" + 
            "\n" + 
            "    .btn-row {\n" + 
            "      margin-top: 10px;\n" + 
            "      text-align: center;\n" + 
            "    }\n" + 
            "\n" + 
            "    .submit-btn {\n" + 
            "      padding: 10px 30px;\n" + 
            "      margin: 5px;\n" + 
            "      font-family: sans-serif;\n" + 
            "      font-size: 12px;\n" + 
            "      text-transform: uppercase;\n" + 
            "      color: #FFF;\n" + 
            "      background-color: #1D6A96;\n" + 
            "      border: none;\n" + 
            "      border-radius: 5px;\n" + 
            "      -webkit-transition: background-color 0.5s ease 0.1s;\n" + 
            "      -o-transition: background-color 0.5s ease 0.1s;\n" + 
            "      transition: background-color 0.5s ease 0.1s;\n" + 
            "    }\n" + 
            "\n" + 
            "    .submit-btn:hover {\n" + 
            "      background-color: #5A92B2;\n" + 
            "    }\n" + 
            "\n" + 
            "    .reset-btn {\n" + 
            "      padding: 10px 30px;\n" + 
            "      margin: 5px;\n" + 
            "      font-family: sans-serif;\n" + 
            "      text-transform: uppercase;\n" + 
            "      font-size: 12px;\n" + 
            "      color: #6C7A82;\n" + 
            "      border: 1px solid #6C7A82;\n" + 
            "      border-radius: 5px;\n" + 
            "      background-color: #F5F5F5;\n" + 
            "      -webkit-transition: color 0.5s ease 0.1s, \n" + 
            "                  border-color 0.5s ease 0.1s;\n" + 
            "      -o-transition: color 0.5s ease 0.1s, \n" + 
            "                  border-color 0.5s ease 0.1s;\n" + 
            "      transition: color 0.5s ease 0.1s, \n" + 
            "                  border-color 0.5s ease 0.1s;\n" + 
            "    }\n" + 
            "\n" + 
            "    .reset-btn:hover {\n" + 
            "      color: #EF476F;\n" + 
            "      border-color: #EF476F;\n" + 
            "    }\n" + 
            "    </style>\n" + 
            "</head>\n" + 
            "<body>\n" + 
            "<div class='page-container'>\n" + 
            "      <div class='form-container'>\n" + 
            "        <form class='login-modal' method='post'>\n" + 
            "          <div class='row-head'>\n" + 
            "            <h1>Site Initialization</h1>\n" + 
            "            <h2>{SITEURL}</h2>\n" +
            "          </div>\n" + 
            "\n" +
            "           {AGENT_KEY_CONTENT}"+
            "          <div class='row'>\n" + 
            "              <label>" + INIT_AUTHORIZATION_FIELD + ":</label><br>\n" + 
            "              <input id='" + INIT_AUTHORIZATION_FIELD + "' name='" + INIT_AUTHORIZATION_FIELD + "' type='text'>\n" + 
            "          </div>\n" +
            //            
            "\n" + 
            "          {DYNAMIC_CONTENT_LINE}" +
            //
            "\n" + 
            "          <div class='btn-row'>\n" + 
            "            <button class='submit-btn' type='submit'>Submit</button>\n" + 
            "          <button class='reset-btn' type='reset'>Reset</button>\n" + 
            "          </div>\n" + 
            "        </form>\n" + 
            "      </div>\n" + 
            "    </div>\n" + 
            "</body>\n" + 
            "</html>"
            + ""
            + "";
    //@formatter:on

}
