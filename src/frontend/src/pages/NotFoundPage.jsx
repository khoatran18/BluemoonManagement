import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="min-h-[60vh] w-full flex items-center justify-center px-6 py-10">
      <div className="max-w-md text-center">
        <div className="text-6xl font-semibold">404</div>
        <h1 className="mt-3 text-xl font-semibold">Không tìm thấy trang</h1>
        <p className="mt-2 text-sm opacity-80">
          Đường dẫn bạn truy cập không tồn tại hoặc đã bị thay đổi.
        </p>

        <div className="mt-6 flex items-center justify-center gap-3">
          <Link
            to="/"
            className="inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium border border-current"
          >
            Về trang chủ
          </Link>
        </div>
      </div>
    </div>
  );
}
