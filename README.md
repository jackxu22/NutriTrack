# NutriTrack Pro – Nutrition Insights Platform

## 项目简介

NutriTrack Pro 是基于 Kotlin 和 Jetpack Compose 开发的个性化营养追踪与分析应用。本项目是 FIT2081 课程的终极作业，旨在扩展早期版本，实现基于本地 Room 数据库的多用户登录系统、与第三方水果数据 API（FruityVice）集成、以及利用 GenAI 生成个性化饮食建议和数据分析。

## 主要功能

- **数据持久化**  
  首次启动时将 CSV 数据导入 Room 数据库，后续仅访问数据库，确保数据安全和快速访问。

- **用户身份管理**  
  多用户登录、首次账号认领（UserID + PhoneNumber + 密码注册），支持自动登录与注销。

- **NutriCoach 界面**  
  - 使用 FruityVice API 显示水果相关信息。  
  - 集成 GenAI（Google Gemini API）生成个性化鼓励语句和饮食小贴士。  
  - 显示和存储历史饮食提示。

- **设置与管理员视图**  
  - 显示当前用户信息，支持注销功能。  
  - 管理员登录进入 Admin View，展示男女用户平均 HEIFA 分数和 AI 生成的饮食行为分析。

- **MVVM 架构**  
  数据访问通过 Repository → ViewModel → UI 层流转，界面响应式绑定 LiveData。

## 技术栈

- **语言与框架**: Kotlin, Jetpack Compose  
- **数据库**: Room (本地持久化)  
- **网络请求**: Retrofit + Kotlin 协程  
- **第三方 API**: FruityVice API, Google Gemini AI API  
- **架构模式**: MVVM (Model-View-ViewModel)  
- **持久存储**: SharedPreferences (登录状态)

## 使用说明

1. **首次运行**：App 自动读取 assets 目录下的 CSV 文件，将数据导入 Room 数据库（仅执行一次）。  
2. **登录流程**：用户首次登录需通过 UserID + PhoneNumber 认领账号，设置密码与姓名。之后登录仅需 UserID + 密码。  
3. **设置界面**：查看个人信息，支持注销和管理员登录。  
4. **管理员视图**：输入特殊密钥 `dollar-entry-apples` 进入，查看数据分析和 AI 报告。

## 致谢

感谢 Monash 大学 FIT2081 课程团队提供项目指导和 API 支持。
