import {jwtDecode} from "jwt-decode";

export const setLocalStorage = (accessToken, refreshToken) => {
    const decodedAccessToken = jwtDecode(accessToken);
    localStorage.setItem("token", accessToken);
    localStorage.setItem("refreshToken", refreshToken)
    localStorage.setItem("email", decodedAccessToken.sub)
    localStorage.setItem("id", decodedAccessToken.id)
    localStorage.setItem("role", decodedAccessToken.role)
    localStorage.setItem("username", decodedAccessToken.username)
    localStorage.setItem("avatar", decodedAccessToken.avatar ? decodedAccessToken.avatar : "default-avatar.png")
    let nextPage;
    if (decodedAccessToken.role === "ADMIN") {
        nextPage = "/admin/dashboard"
    } else {
        nextPage = "/users/dashboard"
    }
    return nextPage;
}