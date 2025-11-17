import { reactive } from 'vue'
/**
 * @typedef {'success' | 'error' | 'warning' | 'info'} SnackbarColor
 */
export default {
    install(app) {
        // 全局共享状态
        const state = reactive({
            show: false,
            message: '',
            color: 'success',
            timeout: 3000,
            location: 'top center'
        })
        /**
         * 打开新的提示
         * @param {string} msg - 显示的消息
         * @param {SnackbarColor} [color='success'] - 颜色，可选：'success' | 'error' | 'warning' | 'info'
         * @param {number} [timeout=3000] - 显示时长
         * @param {string} [location='top center'] - 位置
         */
        // 打开提示
        const show = (msg, color = 'success', timeout = 3000) => {
            state.message = msg
            state.color = color
            state.timeout = timeout
            state.show = true
        }

        // 关闭提示
        const close = () => {
            state.show = false
        }

        // 挂载到全局
        app.config.globalProperties.$snackbar = { state, show, close }
    }
}
