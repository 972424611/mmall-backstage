package com.aekc.mmall.dto;

import com.aekc.mmall.model.SysAcl;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AclDto extends SysAcl {

    /** 是否默认选 */
    private boolean checked = false;

    /** 是否具有权限操作 */
    private boolean hasAcl = false;

    public static AclDto adapt(SysAcl acl) {
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(acl, dto);
        return dto;
    }
}
