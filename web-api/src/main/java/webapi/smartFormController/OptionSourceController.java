package webapi.smartFormController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smartform.dto.PassParameter;
import smartform.widget.model.OptionSource;
import smartform.widget.model.OptionSourceInput;
import smartform.widget.model.OptionSourcePagination;
import smartform.widget.service.OptionSourceService;

import java.util.logging.Logger;

/**
 * @Author: QiHangZhang
 * @Date: 2020/8/18 13:46
 * @Description: 选项源接口
 */
@Api(value="选项源")
@RestController
@RequestMapping("/optionSource")
public class OptionSourceController {
    Logger log = Logger.getLogger(OptionSourceController.class.getName());

    @Autowired
    private OptionSourceService optionSourceService;

    /**
     * 根据ID查询一个选项源
     * @param id
     * @return
     */
    @ApiOperation("根据ID查询")
    @GetMapping(value = "/optionSourceById")
    public OptionSource optionSourceById(String id) {
        return optionSourceService.optionSourceById(id);
    }

    /**
     * 分页查询选项源
     * @param page
     * @return
     */
    @ApiOperation("分页查询")
    @GetMapping(value = "/optionSourceList")
    public OptionSourcePagination optionSourceList(@ModelAttribute OptionSourceInput page) {
        return optionSourceService.optionSourceList(page);
    }

    /**
     * 创建一个选项源
     * @param passParameter
     * @return
     */
    @ApiOperation("创建一个选项源")
    @PostMapping(value = "/createOptionSource")
    public String createOptionSource(@RequestBody PassParameter passParameter) {
        return optionSourceService.createOptionSource(passParameter.getSource());
    }

    /**
     * 更新选项源
     * @param passParameter
     * @return
     */
    @ApiOperation("更新选项源")
    @PostMapping(value = "/updateOptionSource")
    public String updateOptionSource(@RequestBody PassParameter passParameter) {
        return optionSourceService.updateOptionSource(passParameter.getSource());
    }

    /**
     * 发布选项源
     * @param passParameter
     * @return
     */
    @ApiOperation("发布选项源")
    @PostMapping(value = "/updateOptionSourceState")
    public String updateOptionSourceState(@RequestBody PassParameter passParameter) {
        return optionSourceService.updateOptionSourceState(passParameter.getId(), passParameter.getState());
    }

    /**
     * 复制选项源
     * @param passParameter
     * @return
     */
    @ApiOperation("复制选项源")
    @PostMapping(value = "/copyOptionSource")
    public String copyOptionSource(@RequestBody PassParameter passParameter) {
        return optionSourceService.copyOptionSource(passParameter.getId());
    }

    /**
     * 删除选项源
     * @param passParameter
     * @return
     */
    @ApiOperation("删除选项源")
    @PostMapping(value = "/deleteOptionSource")
    public String deleteOptionSource(@RequestBody PassParameter passParameter) {
        return optionSourceService.deleteOptionSource(passParameter.getId());
    }
}
