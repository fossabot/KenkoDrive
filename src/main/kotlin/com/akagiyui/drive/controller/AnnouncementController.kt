package com.akagiyui.drive.controller

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.model.AnnouncementFilter
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.AddAnnouncementRequest
import com.akagiyui.drive.model.request.UpdateAnnouncementRequest
import com.akagiyui.drive.model.response.AnnouncementDisplayResponse
import com.akagiyui.drive.model.response.AnnouncementResponse
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.service.AnnouncementService
import com.akagiyui.drive.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 公告控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/announcement")
class AnnouncementController @Autowired constructor(
    private val announcementService: AnnouncementService,
    private val userService: UserService,
) {
    /**
     * 新增公告
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.ANNOUNCEMENT_ADD)
    fun addAnnouncement(@RequestBody @Validated request: AddAnnouncementRequest): String {
        val announcement = request.toAnnouncement().apply {
            author = userService.getUser()
        }
        return announcementService.addAnnouncement(announcement).id
    }

    /**
     * 获取公告列表
     */
    @GetMapping("", "/")
    @RequirePermission(Permission.ANNOUNCEMENT_GET_ALL)
    fun getAnnouncementList(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute filter: AnnouncementFilter,
    ): PageResponse<AnnouncementResponse> {
        val announcementPage = announcementService.find(index, size, filter)
        val announcementList = announcementPage.content
        val responseList = AnnouncementResponse.fromAnnouncementList(announcementList)
        return PageResponse<AnnouncementResponse>().apply {
            page = index
            this.size = size
            pageCount = announcementPage.totalPages
            total = announcementPage.totalElements
            list = responseList
        }
    }

    /**
     * 获取用于首页展示的公告列表
     */
    @GetMapping("/index")
    @RequirePermission
    fun getIndexAnnouncementList(): List<AnnouncementDisplayResponse> {
        return AnnouncementDisplayResponse.fromAnnouncementList(announcementService.getAnnouncementDisplayList())
    }

    /**
     * 更新公告状态
     *
     * @param id       公告id
     * @param disabled 是否关闭
     */
    @PutMapping("/{id}/status")
    @RequirePermission(Permission.ANNOUNCEMENT_UPDATE)
    fun updateStatus(@PathVariable id: String, @RequestParam(required = false) disabled: Boolean?) {
        disabled?.let { announcementService.disable(id, it) }
    }

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ANNOUNCEMENT_DELETE)
    fun deleteAnnouncement(@PathVariable id: String) {
        announcementService.delete(id)
    }

    /**
     * 修改公告
     *
     * @param id      公告id
     * @param request 更新请求
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.ANNOUNCEMENT_UPDATE)
    fun updateAnnouncement(@PathVariable id: String, @Validated @RequestBody request: UpdateAnnouncementRequest) {
        announcementService.update(id, request)
    }
}
