package com.example.quotation.common;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class BaseEntity {

    @TableField(exist = false)
    private int page;

    @TableField(exist = false)
    private int limit;


    private int total;

    private int offset;



    public int getOffset() {
        return (page - 1) * limit;
    }

    public int getLimit() {
        if(limit <= 0) {
            return 10;
        }
        return limit;
    }

    public int getPage() {
        if(page <= 0) {
            return 1;
        }
        return page;
    }

}
