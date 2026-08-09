# ShapeView-ktx

[![Maven Central](https://img.shields.io/maven-central/v/io.github.impeterwayne/shape-view.svg)](https://central.sonatype.com/artifact/io.github.impeterwayne/shape-view)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg)](https://android-arsenal.com/api?level=23)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Stop writing `res/drawable/*.xml` shape files. Declare corners, gradients, strokes, shadows, ripples and per-state colors **directly on the view, in your layout**.

```xml
<!-- Before: 2 files — bg_button.xml + bg_button_pressed.xml + a selector -->
<Button android:background="@drawable/bg_button_selector" />

<!-- After: 1 view, no drawable files -->
<com.genesys.shape.view.ShapeButton
    app:shape_radius="12dp"
    app:shape_solidColor="#6366F1"
    app:shape_solidPressedColor="#4F46E5" />
```

---

## Table of contents

- [Installation](#installation)
- [Quick start](#quick-start)
- [Recipes](#recipes) — the fastest way to find what you need
- [Which view supports what](#which-view-supports-what)
- [Changing shapes at runtime](#changing-shapes-at-runtime)
- [Attribute reference](#attribute-reference)
- [Gotchas](#gotchas)
- [Requirements & license](#requirements)

---

## Installation

```groovy
dependencies {
    implementation 'io.github.impeterwayne:shape-view:1.0.0'
}
```

---

## Quick start

<p align="center">
  <img src="screenshots/showcase_long.png" width="360" alt="ShapeView-ktx showcase" />
</p>

Swap a platform view for its `Shape*` counterpart and style it inline. Nothing else changes — `ShapeButton` *is* a `Button`, `ShapeLinearLayout` *is* a `LinearLayout`, so all existing attributes, click listeners and view-binding code keep working.

```xml
<com.genesys.shape.view.ShapeButton
    android:layout_width="match_parent"
    android:layout_height="52dp"
    android:text="Continue"
    android:textColor="#FFFFFF"
    app:shape_radius="12dp"
    app:shape_solidColor="#6366F1"
    app:shape_solidPressedColor="#4F46E5"
    app:shape_solidDisabledColor="#C7D2FE" />
```

Three rules cover 90% of usage:

| Prefix | Styles… | Example |
|---|---|---|
| `shape_solid*` | the fill | `shape_solidColor` |
| `shape_stroke*` | the border | `shape_strokeSize`, `shape_strokeColor` |
| `shape_text*` | the text (text views only) | `shape_textPressedColor` |

Any of those three can take a **state suffix** (`Pressed`, `Checked`, `Disabled`, `Focused`, `Selected`) or **gradient colors** instead of a flat color.

---

## Recipes

### Card with rounded corners and a shadow

```xml
<com.genesys.shape.layout.ShapeLinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    app:shape_radius="16dp"
    app:shape_solidColor="#FFFFFF"
    app:shape_outerShadowSize="8dp"
    app:shape_outerShadowColor="#30000000"
    app:shape_outerShadowOffsetY="2dp" />
```

> The shadow is drawn **inside** the view's bounds, so the shape shrinks by `shape_outerShadowSize` on each side. Add matching `android:layout_margin` or extra height if the content looks cramped.

### Button that reacts to press / disable

State colors need no selector XML — just add the suffixed attribute.

```xml
<com.genesys.shape.view.ShapeButton
    android:layout_width="match_parent"
    android:layout_height="52dp"
    android:text="Submit"
    app:shape_radius="26dp"
    app:shape_solidColor="#6366F1"
    app:shape_solidPressedColor="#4F46E5"
    app:shape_solidDisabledColor="#E5E7EB"
    app:shape_textColor="#FFFFFF"
    app:shape_textDisabledColor="#9CA3AF" />
```

Available states: `Pressed` · `Disabled` · `Focused` · `Selected` on every view, plus `Checked` on `ShapeCheckBox` / `ShapeRadioButton`.

### Outlined ("ghost") button and dashed borders

```xml
<!-- Solid 1dp outline that turns indigo while pressed -->
<com.genesys.shape.view.ShapeButton
    android:layout_width="match_parent"
    android:layout_height="52dp"
    android:text="Cancel"
    app:shape_radius="26dp"
    app:shape_solidColor="#00000000"
    app:shape_strokeSize="1dp"
    app:shape_strokeColor="#D1D5DB"
    app:shape_strokePressedColor="#6366F1" />

<!-- Dashed drop zone -->
<com.genesys.shape.layout.ShapeFrameLayout
    android:layout_width="match_parent"
    android:layout_height="120dp"
    app:shape_radius="12dp"
    app:shape_solidColor="#F9FAFB"
    app:shape_strokeSize="2dp"
    app:shape_strokeColor="#9CA3AF"
    app:shape_strokeDashSize="8dp"
    app:shape_strokeDashGap="6dp" />
```

### Input field with a focus highlight

```xml
<com.genesys.shape.view.ShapeEditText
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:hint="Email address"
    android:paddingHorizontal="16dp"
    app:shape_radius="8dp"
    app:shape_solidColor="#FFFFFF"
    app:shape_strokeSize="1dp"
    app:shape_strokeColor="#D1D5DB"
    app:shape_strokeFocusedColor="#6366F1"
    app:shape_solidFocusedColor="#F5F3FF" />
```

### Gradient background

Set a start and an end color; the center color is optional. Orientation defaults to **start → end**.

```xml
<!-- Linear -->
<com.genesys.shape.view.ShapeView
    android:layout_width="match_parent"
    android:layout_height="120dp"
    app:shape_radius="16dp"
    app:shape_solidGradientStartColor="#6366F1"
    app:shape_solidGradientEndColor="#EC4899"
    app:shape_solidGradientOrientation="topLeftToBottomRight" />

<!-- Radial glow, centred slightly above the middle -->
<com.genesys.shape.view.ShapeView
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:shape_type="oval"
    app:shape_solidGradientType="radial"
    app:shape_solidGradientStartColor="#FDE68A"
    app:shape_solidGradientEndColor="#F59E0B"
    app:shape_solidGradientCenterX="0.5"
    app:shape_solidGradientCenterY="0.35"
    app:shape_solidGradientRadiusRatio="0.9" />

<!-- Sweep / conic -->
<com.genesys.shape.view.ShapeView
    android:layout_width="120dp"
    android:layout_height="120dp"
    app:shape_type="oval"
    app:shape_solidGradientType="sweep"
    app:shape_solidGradientStartColor="#6366F1"
    app:shape_solidGradientCenterColor="#EC4899"
    app:shape_solidGradientEndColor="#6366F1" />
```

**Matching a Figma gradient exactly?** Use the extent and stop attributes instead of an orientation enum:

```xml
app:shape_solidGradientStartX="0.1"
app:shape_solidGradientStartY="0.0"
app:shape_solidGradientEndX="0.9"
app:shape_solidGradientEndY="1.0"
app:shape_solidGradientStartPercent="0.15"
app:shape_solidGradientEndPercent="0.85"
```

### Gradient border

The stroke accepts the same gradient options as the fill — swap the `solid` prefix for `stroke`.

```xml
<com.genesys.shape.layout.ShapeFrameLayout
    android:layout_width="match_parent"
    android:layout_height="80dp"
    app:shape_radius="16dp"
    app:shape_solidColor="#111827"
    app:shape_strokeSize="2dp"
    app:shape_strokeGradientStartColor="#6366F1"
    app:shape_strokeGradientEndColor="#EC4899"
    app:shape_strokeGradientOrientation="leftToRight" />
```

### Ripple on anything

Works on every view and layout in the library, not just buttons. Enabling it also makes the view **clickable automatically**.

```xml
<com.genesys.shape.layout.ShapeConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="80dp"
    app:shape_radius="16dp"
    app:shape_solidColor="#FFFFFF"
    app:shape_ripple_enabled="true"
    app:shape_ripple_color="#206366F1" />
```

The ripple is masked to the view's corner radii by default; `shape_ripple_radius` overrides that mask when you want a different rounding.

### Rounded / circular images

`ShapeImageView` clips its own content, so it works with any image loader.

```xml
<!-- Avatar with a ring -->
<com.genesys.shape.view.ShapeImageView
    android:layout_width="64dp"
    android:layout_height="64dp"
    android:scaleType="centerCrop"
    android:src="@drawable/avatar"
    app:shape_type="oval"
    app:shape_strokeSize="2dp"
    app:shape_strokeColor="#6366F1" />

<!-- Card thumbnail: rounded top corners only -->
<com.genesys.shape.view.ShapeImageView
    android:layout_width="match_parent"
    android:layout_height="160dp"
    android:scaleType="centerCrop"
    android:src="@drawable/cover"
    app:shape_radiusInTopLeft="16dp"
    app:shape_radiusInTopRight="16dp" />
```

For RTL-aware corners use the `Start` / `End` variants (`shape_radiusInTopStart`, …) instead of `Left` / `Right`.

### Gradient and outlined text

```xml
<!-- Gradient fill -->
<com.genesys.shape.view.ShapeTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Premium"
    android:textSize="28sp"
    android:textStyle="bold"
    app:shape_textStartColor="#6366F1"
    app:shape_textCenterColor="#A855F7"
    app:shape_textEndColor="#EC4899"
    app:shape_textGradientOrientation="horizontal" />

<!-- Outlined text -->
<com.genesys.shape.view.ShapeTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="OUTLINE"
    android:textSize="32sp"
    app:shape_textColor="#FFFFFF"
    app:shape_textStrokeColor="#111827"
    app:shape_textStrokeSize="2dp" />
```

### Custom CheckBox / RadioButton icons

```xml
<com.genesys.shape.view.ShapeCheckBox
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Remember me"
    app:shape_buttonDrawable="@drawable/ic_check_off"
    app:shape_buttonCheckedDrawable="@drawable/ic_check_on"
    app:shape_buttonDisabledDrawable="@drawable/ic_check_disabled"
    app:shape_textCheckedColor="#6366F1" />
```

`ShapeCheckBox` and `ShapeRadioButton` are also the only views that accept `shape_solidCheckedColor` / `shape_strokeCheckedColor` / `shape_textCheckedColor` — handy for chip-style selectable pills:

```xml
<com.genesys.shape.view.ShapeCheckBox
    android:layout_width="wrap_content"
    android:layout_height="36dp"
    android:button="@null"
    android:gravity="center"
    android:paddingHorizontal="16dp"
    android:text="Design"
    app:shape_radius="18dp"
    app:shape_solidColor="#F3F4F6"
    app:shape_solidCheckedColor="#6366F1"
    app:shape_textColor="#374151"
    app:shape_textCheckedColor="#FFFFFF" />
```

### Rings and dividers

Two shape types that need no extra views:

```xml
<!-- Ring: a donut, sized by ratio or absolute thickness -->
<com.genesys.shape.view.ShapeView
    android:layout_width="80dp"
    android:layout_height="80dp"
    app:shape_type="ring"
    app:shape_solidColor="#6366F1"
    app:shape_ringInnerRadiusRatio="2.5"
    app:shape_ringThicknessSize="6dp" />

<!-- Line: a divider that fills the view and aligns where you want -->
<com.genesys.shape.view.ShapeView
    android:layout_width="match_parent"
    android:layout_height="24dp"
    app:shape_type="line"
    app:shape_strokeSize="1dp"
    app:shape_strokeColor="#E5E7EB"
    app:shape_lineGravity="center" />
```

### Neumorphic bevel (dual inner shadows)

One dark inner shadow plus one light one gives the pressed/embossed look.

```xml
<com.genesys.shape.layout.ShapeConstraintLayout
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:shape_radius="24dp"
    app:shape_solidColor="#E0E5EC"
    app:shape_innerShadowSize="6dp"
    app:shape_innerShadowColor="#40000000"
    app:shape_innerShadowOffsetX="4dp"
    app:shape_innerShadowOffsetY="4dp"
    app:shape_innerShadow2Size="6dp"
    app:shape_innerShadow2Color="#80FFFFFF"
    app:shape_innerShadow2OffsetX="-4dp"
    app:shape_innerShadow2OffsetY="-4dp" />
```

---

## Which view supports what

Every view below supports shape, corners, fill, gradient fill, stroke, gradient stroke, shadows, ring/line options and ripple. The columns show what's **extra**.

| Class | Extends | Text attrs | `Checked` state | Button drawables |
|---|---|:---:|:---:|:---:|
| `view.ShapeView` | `View` | – | – | – |
| `view.ShapeTextView` | `TextView` | ✅ | – | – |
| `view.ShapeButton` | `Button` | ✅ | – | – |
| `view.ShapeEditText` | `EditText` | ✅ | – | – |
| `view.ShapeImageView` | `ImageView` | – | – | – |
| `view.ShapeCheckBox` | `CheckBox` | ✅ | ✅ | ✅ |
| `view.ShapeRadioButton` | `RadioButton` | ✅ | ✅ | ✅ |
| `layout.ShapeLinearLayout` | `LinearLayout` | – | – | – |
| `layout.ShapeFrameLayout` | `FrameLayout` | – | – | – |
| `layout.ShapeRelativeLayout` | `RelativeLayout` | – | – | – |
| `layout.ShapeConstraintLayout` | `ConstraintLayout` | – | – | – |
| `layout.ShapeRadioGroup` | `RadioGroup` | – | – | – |
| `layout.ShapeRecyclerView` | `RecyclerView` | – | – | – |

Full package prefix: `com.genesys.shape.` — e.g. `com.genesys.shape.layout.ShapeConstraintLayout`.

---

## Changing shapes at runtime

Every attribute has a matching setter. Grab the builder, change what you need, then call the matching `into…()` to re-apply — **changes don't take effect until you do**.

```kotlin
// Fill, corners, stroke, shadows → intoBackground()
button.shapeDrawableBuilder
    .setSolidColor(Color.parseColor("#10B981"))
    .setSolidPressedColor(Color.parseColor("#059669"))
    .setRadius(24f)                 // pixels, not dp
    .intoBackground()

// Text colors and gradients → intoTextColor()
textView.textColorBuilder
    .setTextGradientColors(Color.RED, Color.BLUE)
    .intoTextColor()

// CheckBox / RadioButton icons → intoButtonDrawable()
checkBox.buttonDrawableBuilder
    .setButtonCheckedDrawable(drawable)
    .intoButtonDrawable()

// Ripple applies immediately, no into…() needed
card.rippleBuilder.rippleColor = Color.parseColor("#206366F1")
```

| Builder | Accessor | Apply with |
|---|---|---|
| `ShapeDrawableBuilder` | `getShapeDrawableBuilder()` | `intoBackground()` |
| `TextColorBuilder` | `getTextColorBuilder()` | `intoTextColor()` |
| `ButtonDrawableBuilder` | `getButtonDrawableBuilder()` | `intoButtonDrawable()` |
| `RippleBuilder` | `getRippleBuilder()` | applies on set |

Builder setters take **pixels**, so convert dp yourself (`24 * resources.displayMetrics.density`).

---

## Attribute reference

### Shape

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_type` | enum | `rectangle` | `rectangle` · `oval` · `line` · `ring` |
| `shape_width` | dimension | view width | Explicit shape width |
| `shape_height` | dimension | view height | Explicit shape height |

### Corner radius

| Attribute | Format |
|---|---|
| `shape_radius` | dimension — all four corners |
| `shape_radiusInTopLeft` / `shape_radiusInTopStart` | dimension |
| `shape_radiusInTopRight` / `shape_radiusInTopEnd` | dimension |
| `shape_radiusInBottomLeft` / `shape_radiusInBottomStart` | dimension |
| `shape_radiusInBottomRight` / `shape_radiusInBottomEnd` | dimension |

`Start` / `End` variants flip automatically in RTL layouts; `Left` / `Right` do not. A per-corner value overrides `shape_radius`.

### Fill

| Attribute | Format | Notes |
|---|---|---|
| `shape_solidColor` | color | Default state |
| `shape_solidPressedColor` | color | |
| `shape_solidDisabledColor` | color | |
| `shape_solidFocusedColor` | color | |
| `shape_solidSelectedColor` | color | |
| `shape_solidCheckedColor` | color | `ShapeCheckBox` / `ShapeRadioButton` only |

### Fill gradient

Set `StartColor` **and** `EndColor` to switch the fill to a gradient (this replaces `shape_solidColor`). `CenterColor` is optional.

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_solidGradientStartColor` | color | | Start color |
| `shape_solidGradientCenterColor` | color | | Center color (optional) |
| `shape_solidGradientEndColor` | color | | End color |
| `shape_solidGradientType` | enum | `linear` | `linear` · `radial` · `sweep` |
| `shape_solidGradientOrientation` | enum | `startToEnd` | `leftToRight` · `rightToLeft` · `topToBottom` · `bottomToTop` · `topLeftToBottomRight` · `bottomLeftToTopRight` · `topRightToBottomLeft` · `bottomRightToTopLeft`, plus RTL-aware `startToEnd` / `endToStart` / `topStartToBottomEnd` / … |
| `shape_solidGradientCenterX` · `…CenterY` | float | `0.5` | Center of a radial/sweep gradient |
| `shape_solidGradientRadiusSize` | dimension | | Radial radius, absolute |
| `shape_solidGradientRadiusRatio` | float | `0.5` | Radial radius as a ratio of half the shortest side |
| `shape_solidGradientRadiusX` · `…RadiusY` | float | `-1` (off) | Elliptical scale factors |
| `shape_solidRadialAngle` | float | `0` | Radial rotation, degrees |
| `shape_solidGradientStartPercent` | float | `0.0` | Color-stop position of the start color |
| `shape_solidGradientCenterPercent` | float | `0.5` | Color-stop position of the center color |
| `shape_solidGradientEndPercent` | float | `1.0` | Color-stop position of the end color |
| `shape_solidGradientStartX` · `…StartY` | float | orientation | Explicit linear start point, 0–1 of the view |
| `shape_solidGradientEndX` · `…EndY` | float | orientation | Explicit linear end point, 0–1 of the view |

> `shape_solidGradientRadius` is **deprecated** in favour of the `…RadiusSize` / `…RadiusRatio` pair. It still works — a dimension is read as an absolute radius, a float as a ratio — and the explicit pair wins if both are set.

### Stroke

| Attribute | Format | Notes |
|---|---|---|
| `shape_strokeSize` | dimension | Border width; nothing draws without it |
| `shape_strokeColor` | color | Default state |
| `shape_strokePressedColor` | color | |
| `shape_strokeDisabledColor` | color | |
| `shape_strokeFocusedColor` | color | |
| `shape_strokeSelectedColor` | color | |
| `shape_strokeCheckedColor` | color | `ShapeCheckBox` / `ShapeRadioButton` only |
| `shape_strokeDashSize` | dimension | Dash length (`0` = solid) |
| `shape_strokeDashGap` | dimension | Gap between dashes |

### Stroke gradient

Identical to the fill gradient — replace the `solid` prefix with `stroke`:
`shape_strokeGradientStartColor` · `…CenterColor` · `…EndColor` · `…Type` · `…Orientation` · `…CenterX/Y` · `…RadiusSize` · `…RadiusRatio` · `…RadiusX/Y` · `shape_strokeRadialAngle` · `…StartPercent` · `…CenterPercent` · `…EndPercent` · `…StartX/Y` · `…EndX/Y`.

### Shadows

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_outerShadowSize` | dimension | `0` | Outer shadow blur radius |
| `shape_outerShadowColor` | color | `#10000000` | Outer shadow color |
| `shape_outerShadowOffsetX` · `…OffsetY` | dimension | `0` | Outer shadow offset |
| `shape_innerShadowSize` | dimension | `0` | Primary inner shadow blur |
| `shape_innerShadowColor` | color | | Primary inner shadow color |
| `shape_innerShadowOffsetX` · `…OffsetY` | dimension | `0` | Primary inner shadow offset |
| `shape_innerShadow2Size` | dimension | `0` | Secondary inner shadow blur |
| `shape_innerShadow2Color` | color | | Secondary inner shadow color |
| `shape_innerShadow2OffsetX` · `…OffsetY` | dimension | `0` | Secondary inner shadow offset |

### Ring (`shape_type="ring"`)

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_ringInnerRadiusSize` | dimension | `-1` (use ratio) | Inner radius, absolute |
| `shape_ringInnerRadiusRatio` | float | `3.0` | Inner radius = ring size ÷ ratio |
| `shape_ringThicknessSize` | dimension | `-1` (use ratio) | Ring thickness, absolute |
| `shape_ringThicknessRatio` | float | `9.0` | Thickness = ring size ÷ ratio |

### Line (`shape_type="line"`)

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_lineGravity` | flags | `center` | `top` · `bottom` · `left` · `right` · `start` · `end` · `center` |

### Text — text views only

| Attribute | Format | Description |
|---|---|---|
| `shape_textColor` | color | Default text color |
| `shape_textPressedColor` | color | |
| `shape_textDisabledColor` | color | |
| `shape_textFocusedColor` | color | |
| `shape_textSelectedColor` | color | |
| `shape_textCheckedColor` | color | `ShapeCheckBox` / `ShapeRadioButton` only |
| `shape_textStartColor` | color | Gradient start |
| `shape_textCenterColor` | color | Gradient center (optional) |
| `shape_textEndColor` | color | Gradient end |
| `shape_textGradientOrientation` | enum | `horizontal` (default) · `vertical` |
| `shape_textStrokeColor` | color | Text outline color |
| `shape_textStrokeSize` | dimension | Text outline width |

### Button drawables — `ShapeCheckBox` / `ShapeRadioButton` only

`shape_buttonDrawable` · `shape_buttonPressedDrawable` · `shape_buttonCheckedDrawable` · `shape_buttonDisabledDrawable` · `shape_buttonFocusedDrawable` · `shape_buttonSelectedDrawable` — all `reference`.

### Ripple

| Attribute | Format | Default | Description |
|---|---|---|---|
| `shape_ripple_enabled` | boolean | `false` | Enables the ripple **and sets the view clickable** |
| `shape_ripple_color` | color | `#20FFFFFF` | Ripple color |
| `shape_ripple_radius` | dimension | corner radii | Overrides the ripple mask rounding |

---

## Screenshots

A runnable gallery of every feature lives in the `app/` module.

---

## Requirements

| | |
|---|---|
| **Min SDK** | 23 |
| **Compile SDK** | 36 |
| **Language** | Java (fully usable from Kotlin) |

## License

```
Copyright 2026 impeterwayne

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
