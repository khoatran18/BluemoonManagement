import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { login as loginApi } from '../api/authApi';
import { setTokens } from '../auth/tokenStorage';
import AuthLayout from './AuthLayout';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const from = location?.state?.from?.pathname || '/';

  const onSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await loginApi({ username, password });
      if (res?.access_token && res?.refresh_token) {
        setTokens({ accessToken: res.access_token, refreshToken: res.refresh_token });
        navigate(from, { replace: true });
        return;
      }
      setError('Đăng nhập không thành công: thiếu token');
    } catch (err) {
      setError(err?.message || 'Đăng nhập thất bại');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Đăng nhập" subtitle="Vui lòng đăng nhập để tiếp tục.">
      {error ? <div className="auth-error">{error}</div> : null}

      <form onSubmit={onSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        <div className="auth-field">
          <label className="auth-label">Tên đăng nhập</label>
          <input
            className="auth-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </div>

        <div className="auth-field">
          <label className="auth-label">Mật khẩu</label>
          <input
            type="password"
            className="auth-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </div>

        <button type="submit" disabled={loading} className="auth-submit">
          {loading ? 'Đang đăng nhập…' : 'Đăng nhập'}
        </button>
      </form>

      <div className="auth-link">
        Chưa có tài khoản? <Link to="/register">Đăng ký</Link>
      </div>
    </AuthLayout>
  );
}
