package com.meiren.web.acl;

import com.meiren.common.result.VueResult;
import com.meiren.utils.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("{uuid}/acl/upload")
@ResponseBody
public class UploadModule extends BaseController {

    @RequestMapping(value = "imageUpload", method = RequestMethod.POST)
    public VueResult imageUpload(HttpServletRequest request,@RequestParam("file") MultipartFile[] files)
        throws IOException {
        VueResult result = new VueResult();
        Random rd = new Random();
        for(MultipartFile file : files){
            file.transferTo(new File("F://test/"+rd.nextInt(100)+".jpg"));
        }
        return  result;
    }
}
