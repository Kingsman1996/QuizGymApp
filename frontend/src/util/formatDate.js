export const formatDate = (array) => {
    if (!array) return "Không xác định"
    const [year, month, day] = array;
    const dd = String(day).padStart(2, '0');
    const mm = String(month).padStart(2, '0');
    return `${dd}-${mm}-${year}`;
}