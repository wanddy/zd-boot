package webapi.weixinController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import webapi.weixinController.utils.SignUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@RestController("/wx")
public class WeChatCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeChatCheckController.class);


}