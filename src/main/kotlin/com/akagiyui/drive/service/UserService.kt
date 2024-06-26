package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.LoginUserDetails
import com.akagiyui.drive.model.UserFilter
import com.akagiyui.drive.model.request.AddUserRequest
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest
import com.akagiyui.drive.model.request.RegisterConfirmRequest
import com.akagiyui.drive.model.request.UpdateUserInfoRequest
import org.springframework.data.domain.Page
import org.springframework.security.core.userdetails.UserDetailsService

/**
 * 用户服务接口
 * @author AkagiYui
 */
interface UserService : UserDetailsService {
    /**
     * 根据id查找用户
     * @param id 用户id
     * @return 用户
     */
    fun findUserById(id: String): User

    /**
     * 根据id查找用户
     * @param ids 用户id
     * @return 用户
     */
    fun findUserByIds(ids: List<String>): List<User>

    /**
     * 用户注册
     * @param user 用户
     * @return 用户
     */
    fun register(user: User): User

    /**
     * 分页查询用户
     * @return 用户列表
     */
    fun find(index: Int, size: Int, userFilter: UserFilter?): Page<User>

    /**
     * 获取所有用户
     * @return 用户列表
     */
    fun find(): List<User>

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 用户ID
     */
    fun addUser(user: AddUserRequest): User

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    fun delete(id: String)

    /**
     * 用户是否存在
     * @param id 用户ID
     * @return 是否存在
     */
    fun isExist(id: String): Boolean

    /**
     * 从 Security 获取当前用户
     * @return 用户
     */
    fun getUser(): User

    /**
     * 从 redis 或数据库获取当前用户，如果 redis 中不存在则从数据库中获取，并将用户信息存入 redis
     * @param userId 用户ID
     * @return 用户
     */
    fun getUserDetails(userId: String): LoginUserDetails

    /**
     * 发送邮箱验证码
     */
    fun sendEmailVerifyCode(verifyRequest: EmailVerifyCodeRequest)

    /**
     * 确认注册
     *
     * @param registerConfirmRequest 注册确认请求
     */
    fun confirmRegister(registerConfirmRequest: RegisterConfirmRequest)

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @return 密码密文
     */
    fun encryptPassword(username: String, password: String): String

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @param raw 是否不通过加密器加密
     * @return 密码密文
     */
    fun encryptPassword(username: String, password: String, raw: Boolean): String

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     */
    fun updateInfo(userInfo: UpdateUserInfoRequest)

    /**
     * 获取用户权限
     * @return 权限列表
     */
    fun getPermission(): Set<String>

    /**
     * 获取用户角色
     * @return 角色列表
     */
    fun getRole(): Set<String>

    /**
     * 启用/禁用用户
     *
     * @param id       用户 ID
     * @param disabled 是否禁用
     */
    fun disable(id: String, disabled: Boolean)

    /**
     * 重置密码
     *
     * @param id          用户 ID
     * @param newPassword 新密码
     */
    fun resetPassword(id: String, newPassword: String)

    /**
     * 添加角色
     *
     * @param userId 用户ID
     * @param id    角色ID
     */
    fun addRoles(userId: String, id: Set<String>)

    /**
     * 移除角色
     *
     * @param userId 用户ID
     * @param id    角色ID
     */
    fun removeRoles(userId: String, id: Set<String>)

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param userInfo 用户信息
     */
    fun updateInfo(id: String, userInfo: UpdateUserInfoRequest)

    /**
     * 获取用户角色
     *
     * @param id 用户ID
     * @return 角色列表
     */
    fun getRoles(id: String): Set<Role>

}
