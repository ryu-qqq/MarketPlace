# ============================================================================
# Shared SES Email Templates
# ============================================================================
# 셀러 관리 관련 이메일 템플릿을 관리합니다.
# SES v2 API 기반 템플릿으로, ECS 서비스에서 참조합니다.
#
# 템플릿 변수:
#   - seller-approval-invite: {{sellerName}}, {{signUpUrl}}
#   - seller-admin-welcome: {{name}}, {{signUpUrl}}
# ============================================================================

# ========================================
# 셀러 승인 초대 이메일 템플릿
# ========================================
# 입점 승인 후 관리자 가입 안내 이메일
resource "aws_sesv2_email_template" "seller_approval_invite" {
  template_name = "seller-approval-invite"

  template_content {
    subject = "[set-of] {{sellerName}} 입점이 승인되었습니다"
    html    = <<-HTML
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin:0;padding:0;background-color:#f4f4f7;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7;">
    <tr>
      <td align="center" style="padding:40px 0;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);">
          <!-- Header -->
          <tr>
            <td style="background-color:#1a1a2e;padding:32px 40px;text-align:center;">
              <h1 style="margin:0;color:#ffffff;font-size:24px;font-weight:600;">set-of commerce</h1>
            </td>
          </tr>
          <!-- Body -->
          <tr>
            <td style="padding:40px;">
              <h2 style="margin:0 0 16px;color:#1a1a2e;font-size:20px;">입점 승인 완료</h2>
              <p style="margin:0 0 24px;color:#4a4a68;font-size:15px;line-height:1.6;">
                안녕하세요,<br><br>
                <strong style="color:#1a1a2e;">{{sellerName}}</strong>의 입점 신청이 승인되었습니다.<br>
                아래 버튼을 클릭하여 관리자 계정을 생성해 주세요.
              </p>
              <table role="presentation" cellpadding="0" cellspacing="0" style="margin:0 auto 24px;">
                <tr>
                  <td style="background-color:#4f46e5;border-radius:6px;">
                    <a href="{{signUpUrl}}" target="_blank" style="display:inline-block;padding:14px 32px;color:#ffffff;text-decoration:none;font-size:15px;font-weight:600;">관리자 계정 생성</a>
                  </td>
                </tr>
              </table>
              <p style="margin:0 0 8px;color:#8e8ea0;font-size:13px;">버튼이 작동하지 않으면 아래 링크를 브라우저에 직접 입력해 주세요:</p>
              <p style="margin:0 0 24px;color:#4f46e5;font-size:13px;word-break:break-all;">{{signUpUrl}}</p>
              <hr style="border:none;border-top:1px solid #e8e8ed;margin:24px 0;">
              <p style="margin:0;color:#8e8ea0;font-size:13px;line-height:1.5;">
                본 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해 주세요.
              </p>
            </td>
          </tr>
          <!-- Footer -->
          <tr>
            <td style="background-color:#f9f9fb;padding:24px 40px;text-align:center;">
              <p style="margin:0;color:#8e8ea0;font-size:12px;">&copy; set-of commerce. All rights reserved.</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
HTML
    text = <<-TEXT
[set-of] {{sellerName}} 입점이 승인되었습니다

안녕하세요,

{{sellerName}}의 입점 신청이 승인되었습니다.
아래 링크를 클릭하여 관리자 계정을 생성해 주세요.

관리자 계정 생성: {{signUpUrl}}

본 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해 주세요.

(c) set-of commerce. All rights reserved.
TEXT
  }
}

# ========================================
# 셀러 관리자 가입 완료 이메일 템플릿
# ========================================
# 관리자 회원가입 완료 후 이용 안내 이메일
resource "aws_sesv2_email_template" "seller_admin_welcome" {
  template_name = "seller-admin-welcome"

  template_content {
    subject = "[set-of] {{name}}님, 관리자 가입이 완료되었습니다"
    html    = <<-HTML
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin:0;padding:0;background-color:#f4f4f7;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#f4f4f7;">
    <tr>
      <td align="center" style="padding:40px 0;">
        <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="background-color:#ffffff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);">
          <!-- Header -->
          <tr>
            <td style="background-color:#1a1a2e;padding:32px 40px;text-align:center;">
              <h1 style="margin:0;color:#ffffff;font-size:24px;font-weight:600;">set-of commerce</h1>
            </td>
          </tr>
          <!-- Body -->
          <tr>
            <td style="padding:40px;">
              <h2 style="margin:0 0 16px;color:#1a1a2e;font-size:20px;">관리자 가입 완료</h2>
              <p style="margin:0 0 24px;color:#4a4a68;font-size:15px;line-height:1.6;">
                안녕하세요, <strong style="color:#1a1a2e;">{{name}}</strong>님!<br><br>
                관리자 계정 생성이 완료되었습니다.<br>
                아래 버튼을 클릭하여 셀러 관리 페이지로 이동해 주세요.
              </p>
              <table role="presentation" cellpadding="0" cellspacing="0" style="margin:0 auto 24px;">
                <tr>
                  <td style="background-color:#4f46e5;border-radius:6px;">
                    <a href="{{signUpUrl}}" target="_blank" style="display:inline-block;padding:14px 32px;color:#ffffff;text-decoration:none;font-size:15px;font-weight:600;">셀러 관리 페이지</a>
                  </td>
                </tr>
              </table>
              <p style="margin:0 0 8px;color:#8e8ea0;font-size:13px;">버튼이 작동하지 않으면 아래 링크를 브라우저에 직접 입력해 주세요:</p>
              <p style="margin:0 0 24px;color:#4f46e5;font-size:13px;word-break:break-all;">{{signUpUrl}}</p>
              <hr style="border:none;border-top:1px solid #e8e8ed;margin:24px 0;">
              <p style="margin:0;color:#8e8ea0;font-size:13px;line-height:1.5;">
                본 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해 주세요.
              </p>
            </td>
          </tr>
          <!-- Footer -->
          <tr>
            <td style="background-color:#f9f9fb;padding:24px 40px;text-align:center;">
              <p style="margin:0;color:#8e8ea0;font-size:12px;">&copy; set-of commerce. All rights reserved.</p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</html>
HTML
    text = <<-TEXT
[set-of] {{name}}님, 관리자 가입이 완료되었습니다

안녕하세요, {{name}}님!

관리자 계정 생성이 완료되었습니다.
아래 링크를 클릭하여 셀러 관리 페이지로 이동해 주세요.

셀러 관리 페이지: {{signUpUrl}}

본 메일은 발신 전용입니다. 문의사항은 고객센터를 이용해 주세요.

(c) set-of commerce. All rights reserved.
TEXT
  }
}
