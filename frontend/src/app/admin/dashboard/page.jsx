'use client'

import {AdminSidebar} from "../../../components/layout/AdminSidebar"
import {AdminHeader} from "../../../components/layout/AdminHeader"
import UserTable from "../../../components/list/UserTable"
import CategoryTable from "../../../components/list/CategoryTable"
import {useRouter, useSearchParams} from "next/navigation"
import {useEffect} from "react";

export default function Page() {
    const searchParams = useSearchParams()
    const tab = searchParams.get("tab")
    const router = useRouter()
    useEffect(() => {
        if (!tab) {
            router.replace('/admin/dashboard?tab=users')
        }
    }, [tab])

    if (!tab) return null

    return (
        <div className="flex h-screen bg-gray-50">
            <AdminSidebar currentTab={tab}/>
            <div className="flex-1 flex flex-col">
                <AdminHeader/>
                <main className="flex-1 p-6">
                    {tab === "users" && <UserTable/>}
                    {tab === "categories" && <CategoryTable/>}
                </main>
            </div>
        </div>
    )
}
