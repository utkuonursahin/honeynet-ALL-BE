package me.utku.honeynet.dto.email;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.utku.honeynet.model.EmailInfo;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedEmailInfos {

    private List<EmailInfo> emailInfoList;
    private Long currentPage;
    private  Long currentSize;
    private Long totalSize;
    private Long totalPage;

}
