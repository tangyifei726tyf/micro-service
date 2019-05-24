package com.fukun.demo.model.qo.login;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * 登录QO
 *
 * @author tangyifei
 * @since 2019-5-24 15:19:04
 */
@ApiModel("登录QO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginQO {

    @NotBlank
    @ApiModelProperty(value = "账号")
    private String account;

    @NotBlank
    @ApiModelProperty(value = "密码")
    private String pwd;

    @NotEmpty
    @ApiModelProperty(value = "凭证类型")
    private List<String> type;
}
