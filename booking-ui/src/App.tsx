import React, { useEffect, useMemo, useState } from "react";

const API_BASE_URL = "http://localhost:9010";
const GREEN_COLOR = "#00b482";
const WHITE_COLOR = "#ffffff";
const DARK_GREEN = "#00956c";
const LIGHT_GREEN = "#e9fff8";
const BORDER_COLOR = "#d8f5ec";
const TEXT_PRIMARY = "#12332a";
const TEXT_SECONDARY = "#5e7e75";
const PAGE_BACKGROUND = "#f4fbf8";
const DANGER_BG = "#fff1f1";
const DANGER_TEXT = "#c55050";
const ERROR_BORDER = "#f2a6a6";

type Area = {
  id: string;
  name: string;
};

type Desk = {
  id: string;
  number: number;
  area?: Area | null;
};

type Booking = {
  id: string;
  date?: string;
  user?: {
    id: string;
    firstName?: string;
    lastName?: string;
    email?: string;
  } | null;
  desk?: Desk | null;
};

type AuthResponse = {
  token: string;
  role: string;
  userId: string;
};

type Page = "admin" | "booking";

type FieldErrors = Record<string, string>;

type ApiErrorPayload = {
  message?: string;
  fieldErrors?: Record<string, string>;
  timestamp?: string;
};

class ApiError extends Error {
  status: number;
  fieldErrors: FieldErrors;

  constructor(status: number, message: string, fieldErrors: FieldErrors = {}) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request<T>(path: string, token?: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options?.headers ?? {}),
    },
    ...options,
  });

  const contentType = response.headers.get("content-type") || "";

  if (!response.ok) {
    if (contentType.includes("application/json")) {
      const body = (await response.json()) as ApiErrorPayload | FieldErrors;

      const normalized = normalizeApiErrorBody(body);
      throw new ApiError(response.status, normalized.message, normalized.fieldErrors);
    }

    const text = await response.text();
    throw new ApiError(response.status, text || `HTTP ${response.status}`);
  }

  if (!contentType.includes("application/json")) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

function normalizeApiErrorBody(body: ApiErrorPayload | FieldErrors | null | undefined): {
  message: string;
  fieldErrors: FieldErrors;
} {
  if (!body || typeof body !== "object") {
    return {
      message: "Произошла ошибка",
      fieldErrors: {},
    };
  }

  const typedBody = body as ApiErrorPayload;

  if ("message" in typedBody || "fieldErrors" in typedBody) {
    return {
      message: typedBody.message?.trim() || "Произошла ошибка",
      fieldErrors: typedBody.fieldErrors ?? {},
    };
  }

  return {
    message: "Ошибка валидации",
    fieldErrors: body as FieldErrors,
  };
}

export default function App() {
  const [page, setPage] = useState<Page>("booking");

  const [token, setToken] = useState(() => localStorage.getItem("jwt") || "");
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [role, setRole] = useState(() => localStorage.getItem("role") || "");
  const [currentUserId, setCurrentUserId] = useState(() => localStorage.getItem("userId") || "");

  const isAdmin = role === "ADMIN";
  const isUser = role === "USER";
  const isAuthenticated = Boolean(token);

  const [areas, setAreas] = useState<Area[]>([]);
  const [desks, setDesks] = useState<Desk[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [loginFieldErrors, setLoginFieldErrors] = useState<FieldErrors>({});
  const [areaFieldErrors, setAreaFieldErrors] = useState<FieldErrors>({});
  const [deskFieldErrors, setDeskFieldErrors] = useState<FieldErrors>({});
  const [bookingFieldErrors, setBookingFieldErrors] = useState<FieldErrors>({});

  const [newAreaName, setNewAreaName] = useState("");
  const [deskAreaId, setDeskAreaId] = useState("");
  const [deskNumber, setDeskNumber] = useState("");

  const [selectedDeskId, setSelectedDeskId] = useState("");
  const [userId, setUserId] = useState("");
  const [bookingDate, setBookingDate] = useState(() => new Date().toISOString().slice(0, 10));

  const selectedDesk = useMemo(
      () => desks.find((desk) => desk.id === selectedDeskId) ?? null,
      [desks, selectedDeskId]
  );

  const filteredDesksForSelectedArea = useMemo(
      () => desks.filter((desk) => desk.area?.id === deskAreaId),
      [desks, deskAreaId]
  );

  function clearGlobalMessages() {
    setError("");
    setSuccess("");
  }

  function clearAllFieldErrors() {
    setLoginFieldErrors({});
    setAreaFieldErrors({});
    setDeskFieldErrors({});
    setBookingFieldErrors({});
  }

  function resetRequestState() {
    clearGlobalMessages();
    clearAllFieldErrors();
  }

  function applyApiError(
      e: unknown,
      setFormFieldErrors?: React.Dispatch<React.SetStateAction<FieldErrors>>,
      options?: { logoutOnAuth?: boolean }
  ) {
    const logoutOnAuth = options?.logoutOnAuth ?? true;

    if (e instanceof ApiError) {
      setError(e.message);
      setSuccess("");

      if (setFormFieldErrors) {
        setFormFieldErrors(e.fieldErrors ?? {});
      }

      if (logoutOnAuth && (e.status === 401 || e.status === 403)) {
        logout(false);
      }

      return;
    }

    setError(getErrorMessage(e));
    setSuccess("");
  }

  async function loadAll(currentToken = token) {
    if (!currentToken) {
      return;
    }

    try {
      setLoading(true);
      clearGlobalMessages();

      const [areasData, desksData, bookingsData] = await Promise.all([
        request<Area[]>("/areas", currentToken),
        request<Desk[]>("/desks", currentToken),
        request<Booking[]>("/bookings", currentToken),
      ]);

      setAreas(areasData ?? []);
      setDesks(desksData ?? []);
      setBookings(bookingsData ?? []);

      if (!deskAreaId && areasData?.length) {
        setDeskAreaId(areasData[0].id);
      }

      if (!selectedDeskId && desksData?.length) {
        setSelectedDeskId(desksData[0].id);
      }
    } catch (e) {
      applyApiError(e, undefined, { logoutOnAuth: true });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (token) {
      loadAll(token);
    }
  }, [token]);

  async function login() {
    resetRequestState();

    const clientErrors: FieldErrors = {};

    if (!loginEmail.trim()) {
      clientErrors.email = "Введите email";
    }

    if (!loginPassword.trim()) {
      clientErrors.password = "Введите пароль";
    }

    if (Object.keys(clientErrors).length > 0) {
      setLoginFieldErrors(clientErrors);
      setError("Проверьте поля формы");
      return;
    }

    try {
      setLoading(true);

      const response = await request<AuthResponse>("/auth/login", undefined, {
        method: "POST",
        body: JSON.stringify({
          email: loginEmail.trim(),
          password: loginPassword,
        }),
      });

      setToken(response.token);
      setRole(response.role);
      setCurrentUserId(response.userId ?? "");
      localStorage.setItem("jwt", response.token);
      localStorage.setItem("role", response.role);
      localStorage.setItem("userId", response.userId ?? "");
      setSuccess("Вход выполнен");
      setLoginPassword("");
      setLoginFieldErrors({});

      if (response.role === "USER") {
        setUserId(response.userId ?? "");
      }
    } catch (e) {
      applyApiError(e, setLoginFieldErrors, { logoutOnAuth: false });
    } finally {
      setLoading(false);
    }
  }

  function logout(showMessage = true) {
    setToken("");
    setRole("");
    setCurrentUserId("");
    setUserId("");
    localStorage.removeItem("jwt");
    localStorage.removeItem("role");
    localStorage.removeItem("userId");

    setAreas([]);
    setDesks([]);
    setBookings([]);
    setSelectedDeskId("");
    setDeskAreaId("");
    clearAllFieldErrors();
    setError("");
    setSuccess(showMessage ? "Вы вышли из системы" : "");
  }

  async function createArea() {
    resetRequestState();

    const clientErrors: FieldErrors = {};

    if (!newAreaName.trim()) {
      clientErrors.name = "Введите название пространства";
    }

    if (Object.keys(clientErrors).length > 0) {
      setAreaFieldErrors(clientErrors);
      setError("Проверьте поля формы");
      return;
    }

    try {
      await request<Area>("/areas", token, {
        method: "POST",
        body: JSON.stringify({ id: null, name: newAreaName.trim() }),
      });

      setNewAreaName("");
      setSuccess("Пространство создано");
      await loadAll();
    } catch (e) {
      applyApiError(e, setAreaFieldErrors);
    }
  }

  async function deleteArea(id: string) {
    resetRequestState();

    try {
      await request<void>(`/areas/${id}`, token, { method: "DELETE" });
      setSuccess("Пространство удалено");
      await loadAll();
    } catch (e) {
      applyApiError(e);
    }
  }

  async function createDesk() {
    resetRequestState();

    const clientErrors: FieldErrors = {};

    if (!deskAreaId) {
      clientErrors.areaId = "Выберите пространство";
    }

    if (!deskNumber.trim()) {
      clientErrors.number = "Введите номер стола";
    } else {
      const parsedNumber = Number(deskNumber);

      if (Number.isNaN(parsedNumber)) {
        clientErrors.number = "Номер стола должен быть числом";
      } else if (parsedNumber <= 0) {
        clientErrors.number = "Номер стола должен быть больше 0";
      }
    }

    if (Object.keys(clientErrors).length > 0) {
      setDeskFieldErrors(clientErrors);
      setError("Проверьте поля формы");
      return;
    }

    try {
      await request<Desk>("/desks", token, {
        method: "POST",
        body: JSON.stringify({
          id: null,
          areaId: deskAreaId,
          number: Number(deskNumber),
        }),
      });

      setDeskNumber("");
      setSuccess("Стол создан");
      await loadAll();
    } catch (e) {
      applyApiError(e, setDeskFieldErrors);
    }
  }

  async function deleteDesk(id: string) {
    resetRequestState();

    try {
      await request<void>(`/desks/${id}`, token, { method: "DELETE" });
      setSuccess("Стол удалён");
      await loadAll();
    } catch (e) {
      applyApiError(e);
    }
  }

  async function deleteBooking(id: string) {
    resetRequestState();

    try {
      await request<void>(`/bookings/${id}`, token, { method: "DELETE" });
      setSuccess("Бронирование удалено");
      await loadAll();
    } catch (e) {
      applyApiError(e);
    }
  }

  async function createBooking() {
    resetRequestState();

    const effectiveUserId = isUser ? currentUserId : userId.trim();
    const clientErrors: FieldErrors = {};

    if (!effectiveUserId) {
      clientErrors.userId = isUser
          ? "Не удалось определить текущего пользователя"
          : "Введите userId";
    }

    if (!selectedDeskId) {
      clientErrors.deskId = "Выберите стол";
    }

    if (!bookingDate) {
      clientErrors.date = "Выберите дату";
    }

    if (Object.keys(clientErrors).length > 0) {
      setBookingFieldErrors(clientErrors);
      setError("Проверьте поля формы");
      return;
    }

    try {
      await request<Booking>("/bookings", token, {
        method: "POST",
        body: JSON.stringify({
          userId: effectiveUserId,
          deskId: selectedDeskId,
        }),
      });

      setSuccess("Стол забронирован");
      await loadAll();
    } catch (e) {
      applyApiError(e, setBookingFieldErrors);
    }
  }

  if (!isAuthenticated) {
    return (
        <div style={styles.page}>
          <div style={styles.authShell}>
            <section style={styles.authCard}>
              <div style={styles.badge}>OFFICE BOOKING</div>
              <h1 style={styles.title}>Вход в систему</h1>
              <p style={styles.subtitle}>
                Введите email и пароль, чтобы открыть интерфейс бронирования.
              </p>

              {error && <div style={styles.errorBanner}>{error}</div>}
              {success && <div style={styles.successBanner}>{success}</div>}

              <div style={styles.formColumn}>
                <div style={styles.fieldBlock}>
                  <input
                      value={loginEmail}
                      onChange={(e) => setLoginEmail(e.target.value)}
                      placeholder="Email"
                      style={getInputStyle(Boolean(loginFieldErrors.email))}
                  />
                  {loginFieldErrors.email && (
                      <div style={styles.fieldErrorText}>{loginFieldErrors.email}</div>
                  )}
                </div>

                <div style={styles.fieldBlock}>
                  <input
                      type="password"
                      value={loginPassword}
                      onChange={(e) => setLoginPassword(e.target.value)}
                      placeholder="Пароль"
                      style={getInputStyle(Boolean(loginFieldErrors.password))}
                      onKeyDown={(e) => {
                        if (e.key === "Enter") {
                          login();
                        }
                      }}
                  />
                  {loginFieldErrors.password && (
                      <div style={styles.fieldErrorText}>{loginFieldErrors.password}</div>
                  )}
                </div>

                <button onClick={login} style={styles.bookButton} disabled={loading}>
                  {loading ? "Входим..." : "Войти"}
                </button>
              </div>
            </section>
          </div>
        </div>
    );
  }

  return (
      <div style={styles.page}>
        <div style={styles.shell}>
          <header style={styles.headerCard}>
            <div style={styles.headerTopRow}>
              <div>
                <div style={styles.badge}>OFFICE BOOKING</div>
                <h1 style={styles.title}>Управление офисными столами</h1>
                <p style={styles.subtitle}>Бронируй в два клика.</p>
              </div>

              <div style={styles.navGroup}>
                {isAdmin && (
                    <NavButton active={page === "admin"} onClick={() => setPage("admin")}>
                      Админка
                    </NavButton>
                )}
                <NavButton active={page === "booking"} onClick={() => setPage("booking")}>
                  Бронирование
                </NavButton>
                <button onClick={() => logout()} style={styles.dangerButton}>
                  Выйти
                </button>
              </div>
            </div>
          </header>

          <div style={styles.actionsRow}>
            <button onClick={() => loadAll()} style={styles.primaryButton}>
              Обновить данные
            </button>
            {loading && <span style={styles.loadingText}>Загрузка…</span>}
          </div>

          {error && <div style={styles.errorBanner}>{error}</div>}
          {success && <div style={styles.successBanner}>{success}</div>}

          {page === "admin" ? (
              <div style={styles.gridThree}>
                <Card title="Пространства">
                  <div style={styles.formColumn}>
                    <div style={styles.inlineForm}>
                      <input
                          value={newAreaName}
                          onChange={(e) => setNewAreaName(e.target.value)}
                          placeholder="Название пространства"
                          style={getInputStyle(Boolean(areaFieldErrors.name))}
                      />
                      <button onClick={createArea} style={styles.primaryButton}>
                        Создать
                      </button>
                    </div>

                    {areaFieldErrors.name && (
                        <div style={styles.fieldErrorText}>{areaFieldErrors.name}</div>
                    )}
                  </div>

                  <div style={styles.stackList}>
                    {areas.length === 0 && <EmptyState text="Пространств пока нет" />}
                    {areas.map((area) => (
                        <div key={area.id} style={styles.itemCard}>
                          <div>
                            <div style={styles.itemTitle}>{area.name}</div>
                            <div style={styles.itemMeta}>{area.id}</div>
                          </div>
                          <button onClick={() => deleteArea(area.id)} style={styles.dangerButton}>
                            Удалить
                          </button>
                        </div>
                    ))}
                  </div>
                </Card>

                <Card title="Столы">
                  <div style={styles.formColumn}>
                    <div style={styles.fieldBlock}>
                      <select
                          value={deskAreaId}
                          onChange={(e) => setDeskAreaId(e.target.value)}
                          style={getInputStyle(Boolean(deskFieldErrors.areaId))}
                      >
                        <option value="">Выбери пространство</option>
                        {areas.map((area) => (
                            <option key={area.id} value={area.id}>
                              {area.name}
                            </option>
                        ))}
                      </select>
                      {deskFieldErrors.areaId && (
                          <div style={styles.fieldErrorText}>{deskFieldErrors.areaId}</div>
                      )}
                    </div>

                    <div style={styles.inlineForm}>
                      <input
                          value={deskNumber}
                          onChange={(e) => setDeskNumber(e.target.value)}
                          placeholder="Номер стола"
                          style={getInputStyle(Boolean(deskFieldErrors.number))}
                      />
                      <button onClick={createDesk} style={styles.primaryButton}>
                        Создать
                      </button>
                    </div>

                    {deskFieldErrors.number && (
                        <div style={styles.fieldErrorText}>{deskFieldErrors.number}</div>
                    )}
                  </div>

                  <div style={styles.stackList}>
                    {!deskAreaId && <EmptyState text="Сначала выбери пространство" />}
                    {deskAreaId && filteredDesksForSelectedArea.length === 0 && (
                        <EmptyState text="В этом пространстве пока нет столов" />
                    )}
                    {filteredDesksForSelectedArea.map((desk) => (
                        <div key={desk.id} style={styles.itemCard}>
                          <div>
                            <div style={styles.itemTitle}>Стол #{desk.number}</div>
                            <div style={styles.itemSecondary}>
                              Пространство: {desk.area?.name ?? "—"}
                            </div>
                            <div style={styles.itemMeta}>{desk.id}</div>
                          </div>
                          <button onClick={() => deleteDesk(desk.id)} style={styles.dangerButton}>
                            Удалить
                          </button>
                        </div>
                    ))}
                  </div>
                </Card>

                <Card title="Бронирования">
                  <div style={styles.stackList}>
                    {bookings.length === 0 && <EmptyState text="Бронирований пока нет" />}
                    {bookings.map((booking) => (
                        <div key={booking.id} style={styles.bookingCard}>
                          <div style={styles.itemTitle}>Бронь #{booking.id.slice(0, 8)}</div>
                          <div style={styles.bookingMetaBlock}>
                            <div>Стол: {booking.desk?.number ?? "—"}</div>
                            <div>Пространство: {booking.desk?.area?.name ?? "—"}</div>
                            <div>Пользователь: {booking.user?.id ?? "—"}</div>
                            <div>Дата: {formatDateTime(booking.date)}</div>
                          </div>
                          <button
                              onClick={() => deleteBooking(booking.id)}
                              style={styles.dangerButtonWide}
                          >
                            Удалить бронирование
                          </button>
                        </div>
                    ))}
                  </div>
                </Card>
              </div>
          ) : (
              <div style={styles.gridBooking}>
                <Card title="Доступные столы">
                  <div style={styles.bookingFilters}>
                    <div style={styles.fieldBlock}>
                      <label style={styles.label}>Дата бронирования</label>
                      <input
                          type="date"
                          value={bookingDate}
                          onChange={(e) => setBookingDate(e.target.value)}
                          style={getInputStyle(Boolean(bookingFieldErrors.date))}
                      />
                      {bookingFieldErrors.date && (
                          <div style={styles.fieldErrorText}>{bookingFieldErrors.date}</div>
                      )}
                      <p style={styles.helperText}>
                        Поле даты отображается в UI, но текущий backend его не принимает.
                      </p>
                    </div>

                    {isAdmin && (
                        <div style={styles.fieldBlock}>
                          <label style={styles.label}>ID пользователя</label>
                          <input
                              value={userId}
                              onChange={(e) => setUserId(e.target.value)}
                              placeholder="UUID пользователя"
                              style={getInputStyle(Boolean(bookingFieldErrors.userId))}
                          />
                          {bookingFieldErrors.userId && (
                              <div style={styles.fieldErrorText}>{bookingFieldErrors.userId}</div>
                          )}
                        </div>
                    )}

                    {isUser && (
                        <div style={styles.fieldBlock}>
                          <label style={styles.label}>Пользователь</label>
                          <div style={styles.readonlyField}>
                            {currentUserId || "Текущий пользователь не определён"}
                          </div>
                          {bookingFieldErrors.userId && (
                              <div style={styles.fieldErrorText}>{bookingFieldErrors.userId}</div>
                          )}
                        </div>
                    )}
                  </div>

                  <div style={styles.deskGrid}>
                    {desks.length === 0 && <EmptyState text="Столы не найдены" />}
                    {desks.map((desk) => {
                      const isSelected = desk.id === selectedDeskId;
                      const hasBooking = bookings.some((booking) => booking.desk?.id === desk.id);

                      return (
                          <button
                              key={desk.id}
                              onClick={() => setSelectedDeskId(desk.id)}
                              style={{
                                ...styles.deskTile,
                                ...(isSelected ? styles.deskTileActive : {}),
                                ...(bookingFieldErrors.deskId ? styles.deskTileErrorOutline : {}),
                              }}
                          >
                            <div style={styles.deskTileHeader}>
                              <div style={styles.deskTileTitle}>Стол #{desk.number}</div>
                              <span style={hasBooking ? styles.statusBusy : styles.statusFree}>
                          {hasBooking ? "Занят" : "Свободен"}
                        </span>
                            </div>
                            <div style={styles.itemSecondary}>
                              Пространство: {desk.area?.name ?? "—"}
                            </div>
                            <div style={styles.itemMeta}>{desk.id}</div>
                          </button>
                      );
                    })}
                  </div>

                  {bookingFieldErrors.deskId && (
                      <div style={{ ...styles.fieldErrorText, marginTop: "12px" }}>
                        {bookingFieldErrors.deskId}
                      </div>
                  )}
                </Card>

                <Card title="Панель бронирования">
                  <div style={styles.formColumn}>
                    <div style={styles.selectedDeskCard}>
                      <div style={styles.label}>Выбранный стол</div>
                      <div style={styles.selectedDeskTitle}>
                        {selectedDesk ? `Стол #${selectedDesk.number}` : "Стол не выбран"}
                      </div>
                      <div style={styles.itemSecondary}>
                        Пространство: {selectedDesk?.area?.name ?? "—"}
                      </div>
                      <div style={styles.itemSecondary}>Дата: {bookingDate || "—"}</div>
                    </div>

                    <button onClick={createBooking} style={styles.bookButton}>
                      Забронировать
                    </button>

                    <div>
                      <h3 style={styles.sectionCaption}>Все бронирования</h3>
                      <div style={styles.stackList}>
                        {bookings.length === 0 && <EmptyState text="Бронирований пока нет" />}
                        {bookings.map((booking) => (
                            <div key={booking.id} style={styles.itemSoftCard}>
                              <div style={styles.itemTitle}>Стол #{booking.desk?.number ?? "—"}</div>
                              <div style={styles.itemSecondary}>
                                Пространство: {booking.desk?.area?.name ?? "—"}
                              </div>
                              <div style={styles.itemMeta}>
                                Пользователь: {booking.user?.id ?? "—"}
                              </div>
                              <div style={styles.itemMeta}>Дата: {formatDateTime(booking.date)}</div>
                            </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
          )}
        </div>
      </div>
  );
}

function Card({ title, children }: { title: string; children: React.ReactNode }) {
  return (
      <section style={styles.card}>
        <h2 style={styles.cardTitle}>{title}</h2>
        {children}
      </section>
  );
}

function NavButton({
                     active,
                     onClick,
                     children,
                   }: {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
}) {
  return (
      <button
          onClick={onClick}
          style={{
            ...styles.navButton,
            ...(active ? styles.navButtonActive : {}),
          }}
      >
        {children}
      </button>
  );
}

function EmptyState({ text }: { text: string }) {
  return <div style={styles.emptyState}>{text}</div>;
}

function formatDateTime(value?: string) {
  if (!value) {
    return "—";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString("ru-RU");
}

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.message;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return "Произошла ошибка";
}

function getInputStyle(hasError: boolean): React.CSSProperties {
  return {
    ...styles.input,
    ...(hasError ? styles.inputError : {}),
  };
}

const styles: Record<string, React.CSSProperties> = {
  page: {
    minHeight: "100vh",
    background: `linear-gradient(180deg, ${PAGE_BACKGROUND} 0%, ${WHITE_COLOR} 100%)`,
    color: TEXT_PRIMARY,
    fontFamily:
        'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    padding: "32px 20px",
    boxSizing: "border-box",
  },
  shell: {
    maxWidth: "1320px",
    margin: "0 auto",
  },
  authShell: {
    maxWidth: "480px",
    margin: "80px auto 0",
  },
  authCard: {
    background: WHITE_COLOR,
    borderRadius: "28px",
    padding: "32px",
    border: `1px solid ${BORDER_COLOR}`,
    boxShadow: "0 16px 40px rgba(18, 51, 42, 0.06)",
  },
  headerCard: {
    background: `linear-gradient(135deg, ${WHITE_COLOR} 0%, #f7fffc 55%, ${LIGHT_GREEN} 100%)`,
    border: `1px solid ${BORDER_COLOR}`,
    borderRadius: "28px",
    padding: "28px 30px",
    boxShadow: "0 20px 60px rgba(0, 180, 130, 0.10)",
    marginBottom: "24px",
  },
  headerTopRow: {
    display: "flex",
    justifyContent: "space-between",
    gap: "20px",
    alignItems: "flex-start",
    flexWrap: "wrap",
  },
  badge: {
    display: "inline-flex",
    alignItems: "center",
    gap: "8px",
    borderRadius: "999px",
    background: LIGHT_GREEN,
    color: GREEN_COLOR,
    padding: "8px 12px",
    fontSize: "12px",
    fontWeight: 700,
    letterSpacing: "0.08em",
    marginBottom: "14px",
  },
  title: {
    margin: 0,
    fontSize: "38px",
    lineHeight: 1.05,
    fontWeight: 800,
  },
  subtitle: {
    margin: "10px 0 0",
    maxWidth: "760px",
    color: TEXT_SECONDARY,
    fontSize: "15px",
    lineHeight: 1.7,
  },
  navGroup: {
    display: "flex",
    gap: "10px",
    flexWrap: "wrap",
  },
  navButton: {
    border: `1px solid ${BORDER_COLOR}`,
    background: WHITE_COLOR,
    color: TEXT_PRIMARY,
    borderRadius: "18px",
    padding: "12px 18px",
    fontSize: "14px",
    fontWeight: 700,
    cursor: "pointer",
    transition: "all 0.2s ease",
    boxShadow: "0 8px 24px rgba(18, 51, 42, 0.05)",
  },
  navButtonActive: {
    background: GREEN_COLOR,
    color: WHITE_COLOR,
    borderColor: GREEN_COLOR,
    boxShadow: "0 14px 30px rgba(0, 180, 130, 0.25)",
  },
  actionsRow: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
    marginBottom: "18px",
    flexWrap: "wrap",
  },
  loadingText: {
    color: TEXT_SECONDARY,
    fontSize: "14px",
  },
  primaryButton: {
    border: "none",
    background: GREEN_COLOR,
    color: WHITE_COLOR,
    borderRadius: "18px",
    padding: "12px 18px",
    fontSize: "14px",
    fontWeight: 700,
    cursor: "pointer",
    boxShadow: "0 14px 28px rgba(0, 180, 130, 0.22)",
  },
  bookButton: {
    border: "none",
    background: `linear-gradient(135deg, ${GREEN_COLOR} 0%, ${DARK_GREEN} 100%)`,
    color: WHITE_COLOR,
    borderRadius: "20px",
    padding: "15px 18px",
    fontSize: "16px",
    fontWeight: 800,
    cursor: "pointer",
    boxShadow: "0 18px 34px rgba(0, 180, 130, 0.25)",
  },
  dangerButton: {
    border: "none",
    background: DANGER_BG,
    color: DANGER_TEXT,
    borderRadius: "14px",
    padding: "10px 14px",
    fontSize: "13px",
    fontWeight: 700,
    cursor: "pointer",
    whiteSpace: "nowrap",
  },
  dangerButtonWide: {
    border: "none",
    background: DANGER_BG,
    color: DANGER_TEXT,
    borderRadius: "14px",
    padding: "11px 14px",
    fontSize: "13px",
    fontWeight: 700,
    cursor: "pointer",
    width: "100%",
    marginTop: "14px",
  },
  errorBanner: {
    marginBottom: "14px",
    padding: "14px 16px",
    borderRadius: "18px",
    background: DANGER_BG,
    color: DANGER_TEXT,
    border: "1px solid #ffdede",
  },
  successBanner: {
    marginBottom: "14px",
    padding: "14px 16px",
    borderRadius: "18px",
    background: LIGHT_GREEN,
    color: DARK_GREEN,
    border: `1px solid ${BORDER_COLOR}`,
  },
  card: {
    background: WHITE_COLOR,
    borderRadius: "28px",
    padding: "24px",
    border: `1px solid ${BORDER_COLOR}`,
    boxShadow: "0 16px 40px rgba(18, 51, 42, 0.06)",
    minWidth: 0,
  },
  cardTitle: {
    margin: "0 0 20px",
    fontSize: "22px",
    fontWeight: 800,
    letterSpacing: "-0.02em",
  },
  gridThree: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(320px, 1fr))",
    gap: "20px",
  },
  gridBooking: {
    display: "grid",
    gridTemplateColumns: "minmax(0, 1.3fr) minmax(320px, 0.9fr)",
    gap: "20px",
  },
  inlineForm: {
    display: "flex",
    gap: "10px",
    marginBottom: "6px",
    flexWrap: "wrap",
  },
  formColumn: {
    display: "flex",
    flexDirection: "column",
    gap: "14px",
  },
  input: {
    width: "100%",
    boxSizing: "border-box",
    borderRadius: "18px",
    border: `1px solid ${BORDER_COLOR}`,
    background: "#fbfffd",
    padding: "13px 15px",
    fontSize: "14px",
    color: TEXT_PRIMARY,
    outline: "none",
  },
  inputError: {
    border: `1px solid ${ERROR_BORDER}`,
    background: "#fffafa",
    boxShadow: "0 0 0 3px rgba(197, 80, 80, 0.08)",
  },
  fieldErrorText: {
    fontSize: "12px",
    lineHeight: 1.45,
    color: DANGER_TEXT,
    marginTop: "2px",
  },
  readonlyField: {
    width: "100%",
    boxSizing: "border-box",
    borderRadius: "18px",
    border: `1px solid ${BORDER_COLOR}`,
    background: "#f5fdf9",
    padding: "13px 15px",
    fontSize: "14px",
    color: TEXT_PRIMARY,
    wordBreak: "break-all",
  },
  stackList: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  itemCard: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "12px",
    padding: "16px",
    borderRadius: "20px",
    border: `1px solid ${BORDER_COLOR}`,
    background: "#fcfffe",
  },
  itemSoftCard: {
    padding: "14px",
    borderRadius: "18px",
    border: `1px solid ${BORDER_COLOR}`,
    background: "#fcfffe",
  },
  bookingCard: {
    padding: "16px",
    borderRadius: "22px",
    border: `1px solid ${BORDER_COLOR}`,
    background: `linear-gradient(180deg, ${WHITE_COLOR} 0%, #fbfffd 100%)`,
  },
  itemTitle: {
    fontSize: "16px",
    fontWeight: 700,
    color: TEXT_PRIMARY,
  },
  itemSecondary: {
    marginTop: "5px",
    color: TEXT_SECONDARY,
    fontSize: "14px",
  },
  itemMeta: {
    marginTop: "6px",
    color: "#7d9d94",
    fontSize: "12px",
    wordBreak: "break-all",
  },
  bookingMetaBlock: {
    marginTop: "10px",
    display: "grid",
    gap: "6px",
    fontSize: "14px",
    color: TEXT_SECONDARY,
  },
  bookingFilters: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
    gap: "16px",
    marginBottom: "20px",
  },
  fieldBlock: {
    display: "flex",
    flexDirection: "column",
    gap: "8px",
  },
  label: {
    fontSize: "13px",
    fontWeight: 700,
    color: TEXT_SECONDARY,
  },
  helperText: {
    margin: 0,
    fontSize: "12px",
    lineHeight: 1.5,
    color: "#7e8e88",
  },
  deskGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "14px",
  },
  deskTile: {
    textAlign: "left",
    borderRadius: "24px",
    border: `1px solid ${BORDER_COLOR}`,
    background: WHITE_COLOR,
    padding: "18px",
    cursor: "pointer",
    boxShadow: "0 12px 28px rgba(18, 51, 42, 0.04)",
  },
  deskTileActive: {
    border: `1px solid ${GREEN_COLOR}`,
    background: LIGHT_GREEN,
    boxShadow: "0 16px 30px rgba(0, 180, 130, 0.14)",
  },
  deskTileErrorOutline: {
    border: `1px solid ${ERROR_BORDER}`,
  },
  deskTileHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "10px",
    marginBottom: "8px",
  },
  deskTileTitle: {
    fontSize: "18px",
    fontWeight: 800,
    color: TEXT_PRIMARY,
  },
  statusFree: {
    background: LIGHT_GREEN,
    color: DARK_GREEN,
    borderRadius: "999px",
    padding: "7px 10px",
    fontSize: "12px",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },
  statusBusy: {
    background: DANGER_BG,
    color: DANGER_TEXT,
    borderRadius: "999px",
    padding: "7px 10px",
    fontSize: "12px",
    fontWeight: 800,
    whiteSpace: "nowrap",
  },
  selectedDeskCard: {
    padding: "18px",
    borderRadius: "22px",
    background: `linear-gradient(180deg, ${LIGHT_GREEN} 0%, ${WHITE_COLOR} 100%)`,
    border: `1px solid ${BORDER_COLOR}`,
  },
  selectedDeskTitle: {
    marginTop: "8px",
    fontSize: "24px",
    fontWeight: 800,
    color: TEXT_PRIMARY,
  },
  sectionCaption: {
    margin: "0 0 12px",
    fontSize: "12px",
    fontWeight: 800,
    color: TEXT_SECONDARY,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
  },
  emptyState: {
    borderRadius: "18px",
    border: `1px dashed ${BORDER_COLOR}`,
    background: "#fbfffd",
    padding: "18px",
    color: TEXT_SECONDARY,
    fontSize: "14px",
  },
};
