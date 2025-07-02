"use client"

import {useEffect, useState} from "react"
import {useRouter} from "next/navigation"
import {Edit, Mail, Calendar, ArrowLeft} from "lucide-react"
import {formatDate} from "../../util/formatDate";
import UserService from "../../services/UserService";


const Profile = () => {
    const router = useRouter()
    const [loading, setLoading] = useState(true)
    const [userInfo, setUserInfo] = useState({})

    const storedEmail = localStorage.getItem("email")
    const storedRole = localStorage.getItem("role")
    const defaultAvatar = "default-avatar.png"

    const fetchData = async () => {
        try {
            const response = await UserService.getProfile(storedEmail)
            setUserInfo(response.data)
        } catch (error) {
            console.log(error)
        } finally {
            setLoading(false)
        }
    }
    useEffect(() => {
        if (storedEmail === "") {
            router.push("/login")
        } else {
            fetchData()
        }
    }, [])

    const handleBackToDashboard = () => {
        if (storedRole === "ADMIN") {
            router.push("/admin/dashboard")
        } else {
            router.push("/users/dashboard")
        }
    }

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="text-gray-500">Đang tải...</div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Profile Header */}
            <div className="bg-white border-b border-gray-200">
                <div className="max-w-6xl mx-auto px-6 py-8">
                    <div className="flex items-start justify-between">
                        {/* Left side - Avatar, Name, and Edit Button */}
                        <div className="flex items-center space-x-6">
                            <img
                                src={userInfo.avatar || defaultAvatar}
                                alt="Avatar"
                                className="w-40 h-40 rounded-full object-cover border-4 border-gray-200"
                            />
                            <div>
                                <h1 className="text-3xl font-bold text-gray-900 mb-2">{userInfo.username}</h1>
                                <div className="space-y-1 mb-4">
                                    <p className="text-gray-600 flex items-center">
                                        <Mail className="w-4 h-4 mr-2"/>
                                        {storedEmail}
                                    </p>
                                    <p className="text-gray-600 flex items-center">
                                        <Calendar className="w-4 h-4 mr-2"/>
                                        Tham gia: {formatDate(userInfo.createAt) || "---"}
                                    </p>
                                </div>
                                <button
                                    onClick={() => router.push("/profile/edit")}
                                    className="flex items-center space-x-2 px-4 py-2 bg-gray-100 text-gray-800 hover:bg-gray-200 border border-gray-300 rounded-lg cursor-pointer transition-colors text-sm"
                                >
                                    <Edit className="w-4 h-4 text-gray-600"/>
                                    <span className="text-gray-700">Chỉnh sửa Hồ sơ</span>
                                </button>
                            </div>
                        </div>

                        {/* Right side - Back to Dashboard Button */}
                        <div className="flex items-center">
                            <button
                                onClick={() => handleBackToDashboard()}
                                className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive shadow-xs bg-gray-700 text-white hover:bg-gray-600 border border-gray-500 cursor-pointer transition-colors h-9 px-4 py-2"
                            >
                                <ArrowLeft className="w-4 h-4 text-white"/>
                                <span className="text-white">Quay lại</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            {/* Navigation Tabs and Quiz Count - Chỉ hiển thị nếu role khác ADMIN */}
            {userInfo.role !== "ADMIN" && (
                <>
                    <div className="bg-white border-b border-gray-200">
                        <div className="max-w-6xl mx-auto px-6 flex justify-between items-center">
                            <div className="flex space-x-8">
                                <button
                                    className="py-4 px-2 border-b-2 border-purple-600 text-purple-600 font-medium cursor-pointer">
                                    Thư viện
                                </button>
                            </div>
                            <div className="text-center pt-0">
                                <div className="text-3xl font-bold text-gray-900">{0}</div>
                                <div className="text-sm text-gray-600 font-medium">QUIZ</div>
                            </div>
                        </div>
                    </div>

                    {/* Main Content - Chỉ hiển thị nếu role khác ADMIN */}
                    <div className="max-w-6xl mx-auto px-6 py-16">
                        <div className="text-center">
                            {/* Let's Create Section */}
                            <div className="mb-12">
                                <h2 className="text-5xl font-bold text-purple-400 mb-8"
                                    style={{fontFamily: "Comic Sans MS, cursive"}}>
                                    LET'S
                                    <br/>
                                    CREATE!
                                </h2>
                            </div>

                            {/* Description */}
                            <div className="max-w-1xl mx-auto">
                                <h3 className="text-2xl font-bold text-gray-900 mb-4">Tạo bài học đầu tiên của bạn</h3>
                                <p className="text-gray-600 mb-8">
                                    Lấy các câu hỏi từ thư viện hoặc đặt câu hỏi của riêng bạn. Thật nhanh chóng và dễ
                                    dàng!
                                </p>

                                {/* Create Button */}
                                <button
                                    onClick={() => router.push("/users/exams/create")}
                                    className="bg-purple-600 text-white px-6 py-2 rounded-lg text-md font-medium hover:bg-purple-700 cursor-pointer transition-colors"
                                >
                                    Tạo Quiz Mới
                                </button>
                            </div>
                        </div>
                    </div>
                </>
            )}
        </div>
    )
}

export default Profile