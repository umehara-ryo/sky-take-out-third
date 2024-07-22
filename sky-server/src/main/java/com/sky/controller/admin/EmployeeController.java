package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 社員管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api(tags = "社員関連業務")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     *　ログイン
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "社員ログイン")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("社員ログイン：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //ログインしたら、jwtトークンを生成する
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * ログアウト
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("社員ログアウト")
    public Result<String> logout() {

        return Result.success();
    }


    @PostMapping()
    @ApiOperation("社員追加")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {

        log.info("社員追加：{}",employeeDTO);
        employeeService.save(employeeDTO);

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("社員一覧")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){

        log.info("社員一覧",employeePageQueryDTO);
        PageResult  pageResult = employeeService.page(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    @GetMapping("/employee/{id}")
    @ApiOperation("社員検索")
    public Result<Employee> getById(@PathVariable Long id){

        log.info("社員検索",id);
        Employee employee = employeeService.getById(id);

        return Result.success(employee);
    }


    @PostMapping("/status/{status}")
    @ApiOperation("状態変更")
    public Result setStatus(@PathVariable Integer status,Long id){

        log.info("状態変更{},{}",status,id);
        employeeService.setStatus(status,id);

        return Result.success();
    }
}
